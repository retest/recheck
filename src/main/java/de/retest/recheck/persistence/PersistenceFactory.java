package de.retest.recheck.persistence;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import de.retest.recheck.Properties;
import de.retest.recheck.Properties.FileOutputFormat;
import de.retest.recheck.persistence.bin.KryoPersistence;
import de.retest.recheck.persistence.xml.XmlFolderPersistence;
import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.persistence.xml.XmlZipPersistence;

public class PersistenceFactory {

	private final XmlTransformer xml;

	public PersistenceFactory( final Set<Class<?>> xmlDataClasses ) {
		xml = new XmlTransformer( xmlDataClasses );
	}

	public PersistenceFactory( final XmlTransformer xml ) {
		this.xml = xml;
	}

	public <T extends Persistable> Persistence<T> getPersistence() {
		return new DynamicLoadPersistenceProxy<>();
	}

	<T extends Persistable> Persistence<T> getSavePersistenceForIdentifier( final URI identifier ) {
		final FileOutputFormat format = getFormatForIdentifier( identifier );

		switch ( format ) {
			case ZIP:
				return new XmlZipPersistence<>( xml );
			case PLAIN:
				return new XmlFolderPersistence<>( xml );
			case KRYO:
				return new KryoPersistence<>();
			case CLOUD:
				return new CloudPersistence<>();
			default:
				throw new RuntimeException( "Unexpected FileOutputFormat: " + Properties.getFileOutputFormat() );
		}
	}

	<T extends Persistable> Persistence<T> getLoadPersistenceForIdentifier( final URI identifier ) {
		final FileOutputFormat format = getFormatForIdentifier( identifier );

		if ( format == FileOutputFormat.KRYO ) {
			return new KryoPersistence<>();
		} else if ( format == FileOutputFormat.CLOUD ) {
			return new CloudPersistence<>();
		} else if ( new File( identifier ).isDirectory() ) {
			return new XmlFolderPersistence<>( xml );
		} else {
			return new XmlZipPersistence<>( xml );
		}
	}

	private FileOutputFormat getFormatForIdentifier( final URI identifier ) {
		final String filename = FilenameUtils.getName( identifier.getPath() );

		if ( System.getenv().containsKey( CloudPersistence.RECHECK_API_KEY ) ) {
			return FileOutputFormat.CLOUD;
		}

		if ( filename.endsWith( Properties.TEST_REPORT_FILE_EXTENSION ) ) {
			return FileOutputFormat.KRYO;
		}

		return Properties.getFileOutputFormat();
	}

	class DynamicLoadPersistenceProxy<T extends Persistable> implements Persistence<T> {

		@Override
		public void save( final URI identifier, final T element ) throws IOException {
			final Persistence<T> p = getSavePersistenceForIdentifier( identifier );
			p.save( identifier, element );
		}

		@Override
		public T load( final URI identifier ) throws IOException {
			final Persistence<T> p = getLoadPersistenceForIdentifier( identifier );
			return p.load( identifier );
		}
	}
}
