package de.retest.report.action;

import de.retest.ui.actions.ExceptionWrapper;
import de.retest.ui.actions.TargetNotFoundException;
import de.retest.ui.actions.TargetNotFoundWrapper;

public class ErrorHolder {

	private final ExceptionWrapper exceptionWrapper;
	private final TargetNotFoundWrapper targetNotFoundWrapper;

	private ErrorHolder( final ExceptionWrapper exceptionWrapper, final TargetNotFoundWrapper targetNotFoundWrapper ) {
		this.exceptionWrapper = exceptionWrapper;
		this.targetNotFoundWrapper = targetNotFoundWrapper;
	}

	public static ErrorHolder empty() {
		return of( wrapError( null ), wrapTargetNotFound( null ) );
	}

	public static ErrorHolder of( final Throwable exception ) {
		return of( wrapError( exception ), wrapTargetNotFound( null ) );
	}

	public static ErrorHolder of( final TargetNotFoundException tnfe ) {
		return of( wrapError( null ), wrapTargetNotFound( tnfe ) );
	}

	public static ErrorHolder of( final Throwable exception, final TargetNotFoundException tnfe ) {
		return of( wrapError( exception ), wrapTargetNotFound( tnfe ) );
	}

	public static ErrorHolder of( final ExceptionWrapper exception, final TargetNotFoundException tnfe ) {
		return of( exception, wrapTargetNotFound( tnfe ) );
	}

	public static ErrorHolder of( final Throwable exception, final TargetNotFoundWrapper tnfe ) {
		return of( wrapError( exception ), tnfe );
	}

	public static ErrorHolder of( final ExceptionWrapper exception, final TargetNotFoundWrapper tnfe ) {
		return new ErrorHolder( exception, tnfe );
	}

	private static ExceptionWrapper wrapError( final Throwable exception ) {
		return exception != null ? new ExceptionWrapper( exception ) : null;
	}

	private static TargetNotFoundWrapper wrapTargetNotFound( final TargetNotFoundException targetNotFound ) {
		return targetNotFound != null ? new TargetNotFoundWrapper( targetNotFound ) : null;
	}

	public boolean hasThrowable() {
		return exceptionWrapper != null;
	}

	public Throwable getThrowable() {
		return exceptionWrapper != null ? exceptionWrapper.getThrowable() : null;
	}

	public ExceptionWrapper getThrowableWrapper() {
		return exceptionWrapper;
	}

	public boolean hasTargetNotFound() {
		return targetNotFoundWrapper != null;
	}

	public Throwable getTargetNotFoundException() {
		return targetNotFoundWrapper != null ? targetNotFoundWrapper.getTargetNotFoundException() : null;
	}

	public TargetNotFoundWrapper getTargetNotFoundWrapper() {
		return targetNotFoundWrapper;
	}

	public boolean hasError() {
		return exceptionWrapper != null || targetNotFoundWrapper != null;
	}

	@Override
	public String toString() {
		if ( hasThrowable() ) {
			return exceptionWrapper.toString();
		}
		if ( hasTargetNotFound() ) {
			return targetNotFoundWrapper.toString();
		}
		return "no exception";
	}
}
