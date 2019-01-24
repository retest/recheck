package de.retest.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.persistence.Persistable;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Test extends Persistable {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 2;

	@XmlElement( name = "relativeActionSequencePath" )
	private final List<String> relativeActionSequencePaths;

	@SuppressWarnings( "unused" ) // only for JAXB
	private Test() {
		this( new ArrayList<String>() );
	}

	public Test( final List<String> relativeActionSequencePaths ) {
		super( PERSISTENCE_VERSION );
		this.relativeActionSequencePaths = relativeActionSequencePaths;
	}

	public List<String> getRelativeActionSequencePaths() {
		return Collections.unmodifiableList( relativeActionSequencePaths );
	}

}
