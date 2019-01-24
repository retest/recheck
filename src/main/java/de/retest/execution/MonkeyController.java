package de.retest.execution;

import java.io.Serializable;

public interface MonkeyController extends Serializable {

	void log( String msg );

	void setAbortionCondition( AbortableCompoundStoppingCondition condition );

	void stopTestExecution();

	boolean isAborted();

}
