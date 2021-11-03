package de.retest.recheck.ui.diff;

import java.io.Serializable;

public interface LeafDifference extends Difference {

	Serializable getActual();

	Serializable getExpected();

}
