package de.retest.recheck.persistence.xml.util;

import static de.retest.recheck.RecheckProperties.SCREENSHOT_FOLDER_NAME;
import static de.retest.recheck.util.FileUtil.tryReadFromFile;
import static de.retest.recheck.util.FileUtil.tryWriteToFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.util.FileUtil.Reader;
import de.retest.recheck.util.NamedBufferedInputStream;
import de.retest.recheck.util.ReflectionUtilities;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class ScreenshotFolderPersistence {

	private final File screenshotFolder;
	boolean prepared;

	private final Map<Object, Screenshot> parentsWithIncorrectScreenshots = new HashMap<>();

	public ScreenshotFolderPersistence( final File baseFolder ) {
		screenshotFolder = new File( baseFolder, SCREENSHOT_FOLDER_NAME );
		prepared = false;
	}

	private void prepare() {
		if ( !screenshotFolder.exists() ) {
			screenshotFolder.mkdir();
		}
		prepared = true;
	}

	protected static String createFileName( final Screenshot screenshot ) {
		return new File( screenshot.getPersistenceId() ).getName() + "." + screenshot.getType().getFileExtension();
	}

	public Marshaller.Listener getMarshallListener() {
		return new Marshaller.Listener() {
			@Override
			public void afterMarshal( final Object source ) {
				if ( source instanceof Screenshot ) {
					if ( !prepared ) {
						prepare();
					}
					saveScreenshot( (Screenshot) source );
				}
			}
		};
	}

	private void saveScreenshot( final Screenshot screenshot ) {
		final File file = new File( screenshotFolder, createFileName( screenshot ) );

		tryWriteToFile( file, out -> out.write( screenshot.getBinaryData() ) );
	}

	public Unmarshaller.Listener getUnmarshallListener() {
		return new Unmarshaller.Listener() {
			@Override
			public void afterUnmarshal( final Object target, final Object parent ) {
				if ( target instanceof Screenshot ) {
					if ( !loadScreenshot( (Screenshot) target ) ) {
						parentsWithIncorrectScreenshots.put( parent, (Screenshot) target );
					}
				} else if ( parentsWithIncorrectScreenshots.containsKey( target ) ) {
					ReflectionUtilities.setChildInParentToNull( target, parentsWithIncorrectScreenshots.get( target ) );
				}
			}
		};
	}

	private static final class ToByteArrayReader implements Reader<byte[]> {
		@Override
		public byte[] read( final NamedBufferedInputStream in ) throws IOException {
			return IOUtils.toByteArray( in );
		}
	}

	private boolean loadScreenshot( final Screenshot screenshot ) {
		final File file = new File( screenshotFolder, createFileName( screenshot ) );

		final byte[] binaryData = tryReadFromFile( file, new ToByteArrayReader() );

		screenshot.setBinaryData( binaryData );
		return binaryData != null;
	}
}
