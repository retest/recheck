package de.retest.recheck.ui.descriptors.idproviders;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

/**
 * This interface can be implemented by customers to configure how the retest ID is created from the given identifying
 * attributes. For instance, some might prefer a combination of class name and text, or id, or path, or some completely
 * random value (like a UUID).
 */
public interface RetestIdProvider {

	/**
	 * Can be used to configure a custom retest ID provider by setting this property to the fully-qualified name of the
	 * implementing class.
	 */
	public static final String RETEST_ID_PROVIDER_PROPERTY = "de.retest.retestIdProvider";

	/**
	 * Get a retest ID for the given identifying attributes. Ideally, this should be human readable or have some
	 * intuitive relation to the given identifying attributes.
	 *
	 * But it absolutely <em>must</em> be unique within a given state. The {@link #reset()} method is used to reset the
	 * know IDs in between states.
	 *
	 * @param identifyingAttributes
	 *            the identifying attributes of which the ID should be derived from
	 *
	 * @return the generated unique retest ID
	 */
	String getRetestId( IdentifyingAttributes identifyingAttributes );

	/**
	 * Resets the state, such that the same retest ID can be used after calling this method.
	 */
	void reset();

}
