package de.retest.ui.diff;

import java.io.Serializable;
import java.util.List;

public interface Difference extends Serializable {

	int size();

	List<ElementDifference> getNonEmptyDifferences();

	List<ElementDifference> getElementDifferences();

	@Override
	String toString();
}
