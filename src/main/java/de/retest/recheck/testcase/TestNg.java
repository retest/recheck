package de.retest.recheck.testcase;

import java.util.HashSet;
import java.util.Set;

public class TestNg implements TestFramework {

	private final Set<String> annotations = new HashSet<String>();

	public TestNg() {
		annotations.add( "org.testng.annotations.Test" );
		annotations.add( "org.testng.annotations.BeforeClass" );
		annotations.add( "org.testng.annotations.BeforeGroups" );
		annotations.add( "org.testng.annotations.BeforeMethod" );
		annotations.add( "org.testng.annotations.BeforeSuite" );
		annotations.add( "org.testng.annotations.BeforeTest" );
		annotations.add( "org.testng.annotations.AfterClass" );
		annotations.add( "org.testng.annotations.AfterGroups" );
		annotations.add( "org.testng.annotations.AfterMethod" );
		annotations.add( "org.testng.annotations.AfterSuite" );
		annotations.add( "org.testng.annotations.AfterTest" );
	}

	@Override
	public boolean isTestCase( final String annotationName ) {
		return annotations.contains( annotationName );
	}

}
