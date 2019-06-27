package de.retest.recheck;

import java.util.Set;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.RootElement;

/**
 * Interface to help recheck transform an arbitrary object into its internal format to allow persistence, state diffing
 * and ignoring of attributes and elements.
 */
public interface RecheckAdapter {

	/**
	 * Returns {@code true} if the given object can be converted by the adapter.
	 *
	 * @param toVerify
	 *            the object to verify
	 * @return true if the given object can be converted by the adapter
	 */
	boolean canCheck( Object toVerify );

	/**
	 * Convert the given object into a {@code RootElement} (respectively into a set of {@code RootElement}s if this is
	 * sensible for this type of object).
	 *
	 * @param toVerify
	 *            the object to verify
	 * @return The RootElement(s) for the given object
	 */
	Set<RootElement> convert( Object toVerify );

	/**
	 * Returns a {@code DefaultValueFinder} for the converted element attributes. Default values of attributes are
	 * omitted in the Golden Master to not bloat it.
	 *
	 * @return The DefaultValueFinder for the converted element attributes
	 */
	DefaultValueFinder getDefaultValueFinder();

	/**
	 * Notifies about differences in the given {@code ActionReplayResult}.
	 *
	 * @param actionReplayResult
	 *            The {@code ActionReplayResult} containing differences to be notified about.
	 */
	default void notifyAboutDifferences( final ActionReplayResult actionReplayResult ) {}

}
