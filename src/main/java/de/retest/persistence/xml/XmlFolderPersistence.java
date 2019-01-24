package de.retest.persistence.xml;

import static de.retest.util.FileUtil.readFromFile;
import static de.retest.util.FileUtil.writeToFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.Properties;
import de.retest.persistence.Persistable;
import de.retest.persistence.Persistence;
import de.retest.persistence.xml.util.ScreenshotFolderPersistence;
import de.retest.util.FileUtil.Reader;
import de.retest.util.FileUtil.Writer;
import de.retest.util.NamedBufferedInputStream;

public class XmlFolderPersistence<T extends Persistable> implements Persistence<T> {

	private static final Logger logger = LoggerFactory.getLogger( XmlFolderPersistence.class );

	private final XmlTransformer xmlTransformer;
	private final String xmlFileName;

	public XmlFolderPersistence( final XmlTransformer xmlTransformer ) {
		this.xmlTransformer = xmlTransformer;
		xmlFileName = Properties.DEFAULT_XML_FILE_NAME;
	}

	public XmlFolderPersistence( final XmlTransformer xmlTransformer, final String xmlFileName ) {
		this.xmlTransformer = xmlTransformer;
		this.xmlFileName = xmlFileName;
	}

	@Override
	public void save( final URI identifier, final T element ) throws IOException {
		final File baseFolder = new File( identifier );
		final ReTestXmlDataContainer<T> container = new ReTestXmlDataContainer<>( element );

		if ( !baseFolder.exists() ) {
			logger.debug( "baseFolder '{}' don't exists, create new one", baseFolder );
			baseFolder.mkdirs();
		}

		final ScreenshotFolderPersistence screenshotPersistence = new ScreenshotFolderPersistence( baseFolder );

		final File xmlFile = new File( baseFolder, xmlFileName );
		writeToFile( xmlFile, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				xmlTransformer.toXML( container, out, screenshotPersistence.getMarshallListener() );
			}
		} );

	}

	@Override
	public T load( final URI identifier ) throws IOException {
		final File baseFolder = new File( identifier );

		final ScreenshotFolderPersistence screenshotPersistence = new ScreenshotFolderPersistence( baseFolder );

		final File xmlFile = new File( baseFolder, xmlFileName );

		final ReTestXmlDataContainer<T> container = readFromFile( xmlFile, new Reader<ReTestXmlDataContainer<T>>() {
			@Override
			public ReTestXmlDataContainer<T> read( final NamedBufferedInputStream in ) throws IOException {
				return XmlPersistenceUtil.<T> migrateAndRead( xmlTransformer, in,
						screenshotPersistence.getUnmarshallListener() );
			}
		} );

		if ( container == null ) {
			return null;
		}
		return container.data();
	}

	public String getXmlFileName() {
		return xmlFileName;
	}

}
