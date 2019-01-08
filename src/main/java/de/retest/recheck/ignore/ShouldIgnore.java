package de.retest.recheck.ignore;

import java.io.Serializable;

import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;

/**
 * General interface to ignoring changes.
 *
 * Can be implemented by a file, or implementation delivered by the user.
 */
public interface ShouldIgnore {

	/**
	 * Returns <code>true</code> if the element <em>and all of its child elements</em>, so essentially the whole subtree
	 * this element is the root of, should be completely ignored (all attributes of all elements, whether elements are
	 * added or removed).
	 *
	 * @param element
	 *            The element in question.
	 */
	boolean shouldIgnoreElement( final Element element );

	/**
	 * Same as {@link #shouldIgnoreElement(Element)}, but implemented on the basis of the elements
	 * {@link IdentifyingAttributes}.
	 *
	 * Note that if either this method, or the {@link #shouldIgnoreElement(Element)} returning <code>true</code>
	 * suffices, and both are called.
	 */
	boolean shouldIgnoreElement( final IdentifyingAttributes identifyingAttributes );

	/**
	 * Returns <code>true</code> if the given attribute difference as specified by the triple (attribute-key,
	 * expectedValue, actualValue) should be ignored for the given element as specified by its
	 * {@link IdentifyingAttributes}.
	 *
	 * Note that for some elements all values of a given attribute key could be ignored, or an attribute key for all
	 * elements. But sometimes one wants to specify that a certain difference is meaningless, such as
	 * <code>Times Roman</code> vs. <code>Times New Roman</code> for font-family or a 5px difference for outline.
	 */
	boolean shouldIgnoreAttributeDifference( final IdentifyingAttributes element, final String key,
			Serializable expectedValue, Serializable actualValue );

	/**
	 * Same as {@link #shouldIgnoreAttributeDifference(IdentifyingAttributes, String, Serializable, Serializable)}, but
	 * implemented on the basis of the elements retestId.
	 *
	 * Note that if either this method, or the
	 * {@link #shouldIgnoreAttributeDifference(IdentifyingAttributes, String, Serializable, Serializable)} returning
	 * <code>true</code> suffices, and both are called.
	 */
	boolean shouldIgnoreAttributeDifference( final String elementRetestId, final String key, Serializable expectedValue,
			Serializable actualValue );

}
