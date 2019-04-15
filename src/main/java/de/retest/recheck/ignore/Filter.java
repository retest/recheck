package de.retest.recheck.ignore;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

/**
 * General interface to filter changes during Diffing
 *
 * In principal, we create a diff for everything that differs, and put that into the result file. Then it is up to the
 * UI (GUI or CLI) to hide filtered differences – this is what this interface is for. This is pretty much inline with
 * how Git works – the diff is there, it just doesn't show up when filtered.
 *
 * Can be implemented by reading a file, or by an implementation delivered by the user.
 */
public interface Filter {

	/**
	 * Returns <code>true</code> if the element <em>and all of its child elements</em>, so essentially the whole subtree
	 * this element is the root of, should be completely filtered (all attributes of all elements, whether elements are
	 * added or removed).
	 *
	 * @param element
	 *            The element in question.
	 * @return <code>true</code> if the given element should be completely filtered.
	 */
	boolean shouldBeFiltered( final Element element );

	/**
	 * Returns <code>true</code> if the given attribute difference as specified by the triple (attribute-key,
	 * expectedValue, actualValue) should be filtered for the given element as specified by its
	 * {@link IdentifyingAttributes}.
	 *
	 * Note that for some elements all values of a given attribute key could be filtered, or an attribute key for all
	 * elements. But sometimes one wants to specify that a certain difference is meaningless, such as
	 * <code>Times Roman</code> vs. <code>Times New Roman</code> for font-family or a 5px difference for outline.
	 *
	 * @param element
	 *            The element in question.
	 * @param attributeDifference
	 *            The attribute difference for the given element.
	 * @return <code>true</code> if the given attribute difference should be filtered.
	 */
	boolean shouldBeFiltered( final Element element, AttributeDifference attributeDifference );

	public static final Filter FILTER_NOTHING = new Filter() {

		@Override
		public boolean shouldBeFiltered( final Element element ) {
			return false;
		}

		@Override
		public boolean shouldBeFiltered( final Element element, final AttributeDifference attributeDifference ) {
			return false;
		}
	};
}
