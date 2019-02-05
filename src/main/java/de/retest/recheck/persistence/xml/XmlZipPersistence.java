package de.retest.recheck.persistence.xml;

import static de.retest.recheck.Properties.DEFAULT_XML_FILE_NAME;
import static de.retest.recheck.util.FileUtil.readFromZipFile;
import static de.retest.recheck.util.FileUtil.writeToFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.Properties;
import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.persistence.xml.util.LazyScreenshotZipPersistence;
import de.retest.recheck.util.NamedBufferedInputStream;
import de.retest.recheck.util.FileUtil.Writer;
import de.retest.recheck.util.FileUtil.ZipReader;

public class XmlZipPersistence<T extends Persistable> implements Persistence<T> {

	private static final Logger logger = LoggerFactory.getLogger( XmlZipPersistence.class );

	private static final int COMPRESSION_LEVEL = 9;

	private final XmlTransformer xml;

	public XmlZipPersistence( final XmlTransformer xml ) {
		this.xml = xml;
	}

	@Override
	public void save( final URI identifier, final T element ) throws IOException {
		final File file = new File( identifier );
		final ReTestXmlDataContainer<T> container = new ReTestXmlDataContainer<>( element );

		final LazyScreenshotZipPersistence screenshotPersistence = new LazyScreenshotZipPersistence();

		if ( file.isDirectory() ) {
			// this happens if output format was changed from plain to zip
			FileUtils.deleteDirectory( file );
		}

		writeToFile( file, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				final ZipOutputStream zout = new ZipOutputStream( out );
				zout.setLevel( COMPRESSION_LEVEL );
				zout.putNextEntry( new ZipEntry( Properties.DEFAULT_XML_FILE_NAME ) );
				xml.toXML( container, zout, screenshotPersistence.getMarshallListener() );
				logger.debug( "XML saved, now saving screenshots..." );
				screenshotPersistence.saveScreenshotsNow( zout );
				logger.debug( "Save to '{}' completed.", identifier );
				zout.close();
			}
		} );
	}

	@Override
	public T load( final URI identifier ) throws IOException {
		final File file = new File( identifier );

		final LazyScreenshotZipPersistence screenshotPersistence = new LazyScreenshotZipPersistence();

		final ReTestXmlDataContainer<T> container = readFromZipFile( file, new ZipReader<ReTestXmlDataContainer<T>>() {
			@Override
			public ReTestXmlDataContainer<T> read( final ZipFile zipFile ) throws IOException {

				final ReTestXmlDataContainer<T> result = XmlPersistenceUtil.migrateAndRead( xml,
						getReTestXmlInStream( zipFile ), screenshotPersistence.getUnmarshallListener() );

				logger.debug( "XML loaded from file '{}', now loading screenshots.", identifier );
				screenshotPersistence.loadScreenshotsNow( zipFile );

				return result;
			}
		} );

		if ( container == null ) {
			return null;
		}
		return container.data();
	}

	NamedBufferedInputStream getReTestXmlInStream( final ZipFile zipFile ) throws IOException {
		final ZipEntry entry = zipFile.getEntry( DEFAULT_XML_FILE_NAME );
		if ( entry == null ) {
			throw new IllegalArgumentException( "Given ZIP file " + zipFile.getName()
					+ " did not contain an entry named " + DEFAULT_XML_FILE_NAME + "!" );
		}
		return new NamedBufferedInputStream( zipFile.getInputStream( entry ), zipFile.getName() );
	}
}
