package de.retest.recheck.ui.descriptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.ui.review.ActionChangeSet;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class SutState extends Persistable {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 4;

	@XmlElement
	private final List<RootElement> descriptors;

	@SuppressWarnings( "unused" )
	private SutState() {
		super( PERSISTENCE_VERSION );
		// for JAXB
		descriptors = new ArrayList<>();
	}

	public SutState( final Collection<RootElement> set ) {
		super( PERSISTENCE_VERSION );
		if ( set == null ) {
			throw new NullPointerException();
		}
		descriptors = new ArrayList<>( set );
	}

	public List<RootElement> getRootElements() {
		return descriptors;
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

		if ( obj == null || !(obj instanceof SutState) ) {
			return false;
		}

		final SutState other = (SutState) obj;
		return descriptors.equals( other.descriptors );
	}

	@Override
	public String toString() {
		return String.format( "State[descriptor=" + descriptors + "]" );
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
		return new SutState( descriptors );
	}
}
