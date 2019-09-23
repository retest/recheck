package de.retest.recheck.persistence;

/**
 * The naming strategy for both the Golden Master files and the report files.
 *
 * The NamingStrategy itself can come up with names for tests and suites. The default implementation
 * {@link ClassAndMethodBasedNamingStrategy} derives test suite and test case name from the test class and test method,
 * working for both JUnit and TestNG.
 */
public interface NamingStrategy {

	/**
	 * Determines the suite name. In JUnit, for example, this is represented by the class name.
	 *
	 * @return name of the current test suite
	 */
	String getSuiteName();

	/**
	 * Determines the test name. In JUnit, for example, this is represented by the method name.
	 *
	 * @return name of the current test
	 */
	String getTestName();
}
