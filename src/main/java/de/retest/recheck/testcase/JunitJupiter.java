package de.retest.recheck.testcase;

import java.util.HashSet;
import java.util.Set;

public class JunitJupiter implements TestFramework {

	private final Set<String> annotations = new HashSet<String>();

	public JunitJupiter() {
		annotations.add( "org.junit.jupiter.api.Test" );
		annotations.add( "org.junit.jupiter.api.BeforeEach" );
		annotations.add( "org.junit.jupiter.api.BeforeAll" );
		annotations.add( "org.junit.jupiter.api.AfterEach" );
		annotations.add( "org.junit.jupiter.api.AfterAll" );
	}

	@Override
	public boolean isTestCase( final String annotationName ) {
		return annotations.contains( annotationName );
	}

}
