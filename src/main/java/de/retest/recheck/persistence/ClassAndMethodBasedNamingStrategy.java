package de.retest.recheck.persistence;

import de.retest.recheck.TestCaseFinder;

/**
 * The default implementation that derives test suite and test case name from the test class and test method, working
 * for both JUnit and TestNG.
 */
public class ClassAndMethodBasedNamingStrategy implements NamingStrategy {

	@Override
	public String getSuiteName() {
		return TestCaseFinder.getInstance() //
				.findTestCaseClassNameInStack() //
				.orElseThrow( () -> new IllegalStateException( "Couldn't identify test class in call stack.\n"
						+ "This is needed to dynamically name the Golden Master files.\n "
						+ "Please instantiate RecheckImpl with RecheckOptions, and provide a different FileNamerStrategy." ) );
	}

	@Override
	public String getTestName() {
		return TestCaseFinder.getInstance() //
				.findTestCaseMethodNameInStack() //
				.orElseThrow( () -> new IllegalStateException( "Couldn't identify test method in call stack.\n"
						+ "This is needed to dynamically name the Golden Master files.\n"
						+ "Please call `startTest(\"name\")`, giving an explicit name, "
						+ "or instantiate RecheckImpl with RecheckOptions, and provide a different FileNamerStrategy." ) );
	}
}
