package de.retest.ui.actions;

import java.io.Serializable;

/**
 * This is a lightweight class that contains the duration of the execution and the resulting error, if one occurred.
 */
public class ActionExecutionResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ExceptionWrapper error;
	private final TargetNotFoundException targetNotFound;
	private final long duration;

	public ActionExecutionResult( final ExceptionWrapper error, final TargetNotFoundException targetNotFound,
			final long duration ) {
		this.error = error;
		this.targetNotFound = targetNotFound;
		this.duration = duration;

	}

	public ActionExecutionResult( final ExceptionWrapper error, final long duration ) {
		this( error, null, duration );
	}

	public ActionExecutionResult( final TargetNotFoundException targetNotFound ) {
		this( null, targetNotFound, -1 );
	}

	public ExceptionWrapper getError() {
		return error;
	}

	public TargetNotFoundException getTargetNotFound() {
		return targetNotFound;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		String result = "execution took " + duration + "ms";
		if ( getError() != null ) {
			result += " and resulted in " + error.toString();
		}
		if ( getTargetNotFound() != null ) {
			result += " and did not find target " + targetNotFound.toString();
		}
		return result;
	}
}
