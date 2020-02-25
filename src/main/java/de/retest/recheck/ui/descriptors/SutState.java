package de.retest.recheck.ui.descriptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.meta.MetadataProvider;
import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.ui.review.ActionChangeSet;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class SutState extends Persistable {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 4;

	@XmlElement
	private final List<RootElement> descriptors;

	@XmlElement
	private final Map<String, String> metadata;

	@SuppressWarnings( "unused" )
	private SutState() {
		super( PERSISTENCE_VERSION );
		// for JAXB
		descriptors = new ArrayList<>();
		metadata = new HashMap<>();
	}

	public SutState( final Collection<RootElement> set ) {
		this( set, MetadataProvider.empty() );
	}

	public SutState( final Collection<RootElement> set, final MetadataProvider metadata ) {
		super( PERSISTENCE_VERSION );
		if ( set == null ) {
			throw new NullPointerException();
		}
		descriptors = new ArrayList<>( set );
		this.metadata = new HashMap<>( metadata.retrieve() );
	}

	public List<RootElement> getRootElements() {
		return descriptors;
	}

	public Map<String, String> getMetadata() {
		return Collections.unmodifiableMap( metadata );
	}

	public String getMetadata( final String key ) {
		return metadata.get( key );
	}

	@Override
	public int hashCode() {
		return descriptors.hashCode();
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}

		if ( !(obj instanceof SutState) ) {
			return false;
		}

		final SutState other = (SutState) obj;
		return descriptors.equals( other.descriptors );
	}

	@Override
	public String toString() {
		return "State" + descriptors;
	}

	public SutState applyChanges( final ActionChangeSet actionChangeSet ) {
		if ( actionChangeSet == null ) {
			return this;
		}
		final List<RootElement> descriptors = new ArrayList<>();
		for ( final RootElement rootElement : getRootElements() ) {
			descriptors.add( rootElement.applyChanges( actionChangeSet ) );
		}
		for ( final Element element : actionChangeSet.getInsertedChanges() ) {
			if ( element instanceof RootElement ) {
				descriptors.add( (RootElement) element );
			}
		}
		final Map<String, String> newMetadata = new HashMap<>( metadata );
		newMetadata.putAll( actionChangeSet.getMetadata() );
		return new SutState( descriptors, () -> newMetadata );
	}
}
