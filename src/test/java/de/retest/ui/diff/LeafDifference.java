package de.retest.ui.diff;

import java.io.Serializable;

public interface LeafDifference extends Difference {

	public Serializable getActual();

	public Serializable getExpected();

}
