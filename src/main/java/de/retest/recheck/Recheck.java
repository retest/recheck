package de.retest.recheck;

/**
 * Interface defining the core functionality to write tests with recheck.
 * 
 * @apiNote Provided {@link RecheckOptions} should be honored and fully supported.
 */
public interface Recheck extends RecheckLifecycle {

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

}
