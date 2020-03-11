package de.retest.recheck.ignore;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

/**
 * General interface to filter changes during Diffing.
 *
 * The Filter does not define positive or negative filtering, i.e. wether only matching elements are shown or only
 * elements that do not match.
 *
 * In principal, we create a diff for everything that differs, and put that into the result file. Then it is up to the
 * UI (GUI or CLI) to hide filtered differences – this is what this interface is for. This is pretty much inline with
 * how Git works – the diff is there, it just doesn't show up when filtered.
 *
 * Can be implemented by reading a file, or by an implementation delivered by the user.
 */
public interface Filter {

	public enum ChangeType {
		INSERTED,
		DELETED,
		CHANGED;
	}

	/**
	 * Returns <code>true</code> if the element <em>and all of its child elements</em>, so essentially the whole subtree
	 * this element is the root of, should be completely filtered (all attributes of all elements, whether elements are
	 * added or removed).
	 *
	 * @param element
	 *            The element in question.
	 * @return <code>true</code> if the given element should be completely filtered.
	 */
	boolean matches( final Element element );

	/**
	 * Returns <code>true</code> if the element <em>and all of its child elements</em>, so essentially the whole subtree
	 * this element is the root of, should be completely filtered (all attributes of all elements, whether elements are
	 * added or removed).
	 *
	 * If {@link #matches(Element)} returns <code>true</code>, this method must always return <code>true</code>. Can
	 * return any value only if {@link #matches(Element)} returns <code>false</code>.
	 *
	 * @param element
	 *            The element in question.
	 * @param change
	 *            The type of the change (see {@link ChangeType}).
	 * @return <code>true</code> if the given element should be completely filtered.
	 */
	default boolean matches( final Element element, final ChangeType change ) {
		return matches( element );
	}

	/**
	 * Returns <code>true</code> if the given attribute as specified by the attribute-key should be filtered for the
	 * given element.
	 *
	 * If {@link #matches(Element)} returns <code>true</code>, this method must always return <code>true</code>. Can
	 * return any value only if {@link #matches(Element)} returns <code>false</code>.
	 *
	 * @param element
	 *            The element in question.
	 * @param attributeKey
	 *            The attribute-key for the given element.
	 * @return <code>true</code> if the given attribute should be filtered.
	 */
	default boolean matches( final Element element, final String attributeKey ) {
		return matches( element );
	}

	/**
	 * Returns <code>true</code> if the given attribute difference as specified by the triple (attribute-key,
	 * expectedValue, actualValue) should be filtered for the given element as specified by its
	 * {@link IdentifyingAttributes}.
	 *
	 * Note that for some elements all values of a given attribute key could be filtered, or an attribute key for all
	 * elements. But sometimes one wants to specify that a certain difference is meaningless, such as
	 * <code>Times Roman</code> vs. <code>Times New Roman</code> for font-family or a 5px difference for outline.
	 *
	 * If {@link #matches(Element,String)} returns <code>true</code>, this method must always return <code>true</code>.
	 * Can return any value only if {@link #matches(Element,String)} returns <code>false</code>.
	 *
	 * @param element
	 *            The element in question.
	 * @param attributeDifference
	 *            The attribute difference for the given element.
	 * @return <code>true</code> if the given attribute difference should be filtered.
	 */
	default boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return matches( element, attributeDifference.getKey() );
	}

	/**
	 * A special {@link Filter} that never matches.
	 */
	Filter NEVER_MATCH = element -> false;

	/**
	 * A special {@link Filter} that always matches.
	 */
	Filter ALWAYS_MATCH = element -> true;

	/**
	 * Use {@link #NEVER_MATCH} instead.
	 */
	// TODO Remove for a 2.0 release
	@Deprecated
	public static final Filter FILTER_NOTHING = NEVER_MATCH;

}
