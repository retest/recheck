package de.retest.recheck.testcase;

import java.util.HashSet;
import java.util.Set;

public class JunitVintage implements TestFramework {

	private final Set<String> annotations = new HashSet<String>();

	public JunitVintage() {
		annotations.add( "org.junit.Test" );
		annotations.add( "org.junit.Before" );
		annotations.add( "org.junit.BeforeClass" );
		annotations.add( "org.junit.After" );
		annotations.add( "org.junit.AfterClass" );
		annotations.add( "org.junit.experimental.theories.Theory" );
	}

	@Override
	public boolean isTestCase( final String annotationName ) {
		return annotations.contains( annotationName );
	}

}
