package de.retest.recheck.persistence;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Set;

import de.retest.persistence.xml.XmlFolderPersistence;
import de.retest.recheck.RecheckAdapter;
import de.retest.ui.descriptors.RootElement;
import de.retest.ui.descriptors.SutState;

public class RecheckSutState {

	private static final XmlFolderPersistence<SutState> sutStatePersistence =
			new XmlFolderPersistence<>( getXmlTransformer() );

	private RecheckSutState() {

	}

	public static SutState convert( final Object toVerify, final RecheckAdapter adapter ) {
		final Set<RootElement> converted = adapter.convert( toVerify );
		if ( converted == null || converted.isEmpty() ) {
			throw new IllegalStateException( "Cannot check empty state!" );
		}
		return new SutState( converted );
	}

	public static SutState createNew( final File file, final SutState actual ) {
		try {
			sutStatePersistence.save( file.toURI(), actual );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not save sut state '" + actual + "' to '" + file + "'.", e );
		}
		return new SutState( new ArrayList<>() );
	}

	public static SutState loadExpected( final File file ) {
		if ( !file.exists() ) {
			return null;
		}
		// Folder could exist, but not the retest.xml...
		if ( !new File( file, sutStatePersistence.getXmlFileName() ).exists() ) {
			return null;
		}
		try {
			return sutStatePersistence.load( file.toURI() );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not load sut state from '" + file + "'.", e );
		}
	}
}
