package de.retest.recheck;

/**
 * Interface defining the lifecycle functionality to write tests with recheck.
 *
 * @apiNote Provided {@link RecheckOptions} should be honored and fully supported.
 */
public interface RecheckLifecycle {

	/**
	 * Start a test and infer the test name from the calling method.
	 */
	void startTest();

	/**
	 * Tells recheck that a test has started. This helps to check multiple states within a single test.
	 *
	 * @param testName
	 *            the name of the test
	 */
	void startTest( String testName );

	/**
	 * Tells recheck that the test has finished. If one or more checks during this tests resulted in differences, this
	 * triggers an {@link AssertionError} to be thrown like any other assertion would. Still, the differences are
	 * persisted as test reports into the target folder.
	 */
	void capTest() throws AssertionError;

	/**
	 * Finishes recheck and persists the test report. Starting the review GUI is sensible.
	 */
	void cap();

}
