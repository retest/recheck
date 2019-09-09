package de.retest.recheck.persistence;

/**
 * The naming strategy for both the Golden Master files and the report files.
 *
 * The NamingStrategy itself can come up with names for tests and suites. The default implementation (@link
 * {@link JUnitTestbasedNamingStrategy}) derives test suite and test case name from the test class and test method,
 * working for both JUnit and TestNG.
 *
 * It also contains a {@link FileNamer}, that from these values can create files for read or write operations. Note that
 * these can be mixed and matched, e.g. one can have a {@link JUnitTestbasedShortNamingStrategy} that returns a
 * {@link GradleProjectLayout}.
 */
public interface NamingStrategy {

	/**
	 * Determines the suite name.
	 *
	 * @return name of the current test suite
	 */
	String getSuiteName();

	/**
	 * Determines the test name.
	 *
	 * @return name of the current test
	 */
	String getTestName();
}
