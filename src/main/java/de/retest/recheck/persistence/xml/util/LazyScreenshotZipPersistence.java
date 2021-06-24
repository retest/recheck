package de.retest.recheck.persistence.xml.util;

import static de.retest.recheck.RecheckProperties.SCREENSHOT_FOLDER_NAME;
import static de.retest.recheck.RecheckProperties.ZIP_FOLDER_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.util.ReflectionUtilities;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class LazyScreenshotZipPersistence {

	private static final Logger logger = LoggerFactory.getLogger( LazyScreenshotZipPersistence.class );

	/** package visible only for test */
	final List<Screenshot> screenshots = new CopyOnWriteArrayList<>();
	final Multimap<Screenshot, Object> screenshotParentMap = ArrayListMultimap.create();

	public Marshaller.Listener getMarshallListener() {
		return new Marshaller.Listener() {
			@Override
			public void afterMarshal( final Object source ) {
				if ( source instanceof Screenshot ) {
					screenshots.add( (Screenshot) source );
				}
			}
		};
	}

	public void saveScreenshotsNow( final ZipOutputStream zout ) {
		final List<String> beforePersistedIds = new ArrayList<>();
		for ( final Screenshot screenshot : screenshots ) {
			if ( beforePersistedIds.contains( screenshot.getPersistenceId() ) ) {
				logger.debug( "Image already persisted (duplicate): {}", screenshot.getPersistenceId() );
			} else {
				saveScreenshot( zout, screenshot );
				beforePersistedIds.add( screenshot.getPersistenceId() );
			}
		}
	}

	private void saveScreenshot( final ZipOutputStream zout, final Screenshot screenshot ) {
		if ( screenshot.getBinaryData() != null ) {
			try {
				zout.putNextEntry( new ZipEntry( createFilePath( screenshot ) ) );
				zout.write( screenshot.getBinaryData() );
			} catch ( final IOException e ) {
				logger.error( "Error writing entry {} to zip file: {}.", screenshot, e.getMessage() );
			}
		} else {
			logger.warn( "Found screenshot without data! {}", screenshot );
		}
	}

	public Unmarshaller.Listener getUnmarshallListener() {
		return new Unmarshaller.Listener() {
			@Override
			public void afterUnmarshal( final Object target, final Object parent ) {
				if ( target instanceof Screenshot ) {
					screenshots.add( (Screenshot) target );
					screenshotParentMap.put( (Screenshot) target, parent );
				}
			}
		};
	}

	public void loadScreenshotsNow( final ZipFile zipFile ) throws IOException {
		for ( final Screenshot screenshot : screenshots ) {
			final String path = createFilePath( screenshot );
			try {
				final ZipEntry entry = zipFile.getEntry( path );
				final InputStream in = zipFile.getInputStream( entry );
				screenshot.setBinaryData( IOUtils.toByteArray( in ) );
			} catch ( final Exception exc ) {
				logger.warn( "Could not load screenshot '{}' from {}.", path, zipFile.getName() );
				for ( final Object parent : screenshotParentMap.get( screenshot ) ) {
					ReflectionUtilities.setChildInParentToNull( parent, screenshot );
				}
			}
		}
	}

	protected static String createFilePath( final Screenshot screenshot ) {
		return SCREENSHOT_FOLDER_NAME + ZIP_FOLDER_SEPARATOR + new File( screenshot.getPersistenceId() ).getName() + "."
				+ screenshot.getType().getFileExtension();
	}

}
