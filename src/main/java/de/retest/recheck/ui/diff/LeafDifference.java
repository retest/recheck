package de.retest.recheck.ui.diff;

import java.io.Serializable;

public interface LeafDifference extends Difference {

	public Serializable getActual();

	public Serializable getExpected();

}
