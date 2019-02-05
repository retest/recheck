package de.retest.recheck.suite;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.persistence.Persistable;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class CapturedSuite extends Persistable {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 2;

	@XmlElement( name = "tests" )
	private final ArrayList<String> tests = new ArrayList<>();

	public CapturedSuite() {
		super( PERSISTENCE_VERSION );
	}

	public ArrayList<String> getTests() {
		return tests;
	}

	public void addTest( final String relativeFilePath ) {
		tests.add( relativeFilePath );
	}

	public void addTests( final String... testFileNames ) {
		for ( final String test : testFileNames ) {
			tests.add( test );
		}
	}
}
