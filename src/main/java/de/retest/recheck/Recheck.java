package de.retest.recheck;

/**
 * Interface defining the core functionality to write tests with recheck.
 * 
 * @apiNote Provided {@link RecheckOptions} should be honored and fully supported.
 */
public interface Recheck {

	/**
	 * Checks the given object against the persisted golden master. The step name is a semantic string to later identify
	 * the associated golden master.
	 *
	 * If this is the first time the object is being checked, it is persisted as is to create a golden master to check
	 * against.
	 *
	 * @param toVerify
	 *            the object to verify
	 * @param stepName
	 *            a semantic string to later identify the associated golden master
	 * @throws IllegalStateException
	 *             if no {@code RecheckAdapter} for the given object can be found
	 */
	void check( Object toVerify, String stepName ) throws IllegalStateException;

	/**
	 * Checks the given object against the persisted golden master using the given adapter (since they sometimes can be
	 * ambiguous, e.g. a stream). The step name is a semantic string to later identify the associated golden master.
	 *
	 * If this is the first time the object is being checked, it is persisted as is to create a golden master to check
	 * against.
	 *
	 * @param toVerify
	 *            the object to verify
	 * @param adapter
	 *            the adapter to be used
	 * @param stepName
	 *            a semantic string to later identify the associated golden master
	 * @throws IllegalStateException
	 *             if no RecheckAdapter for the given object can be found
	 */
	void check( Object toVerify, RecheckAdapter adapter, String stepName ) throws IllegalArgumentException;

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
