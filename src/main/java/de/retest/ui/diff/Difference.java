package de.retest.ui.diff;

import java.io.Serializable;
import java.util.List;

public interface Difference extends Serializable {

	int size();

	/**
	 * @return non-empty differences for attribute and identifying attribute differences as well as non-empty child
	 *         differences
	 */
	List<ElementDifference> getNonEmptyDifferences();

	/**
	 * @return all differences for attribute and identifying attribute differences as well as all child differences
	 */
	List<ElementDifference> getElementDifferences();

	@Override
	String toString();
}
