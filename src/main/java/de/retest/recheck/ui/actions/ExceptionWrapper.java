package de.retest.recheck.ui.actions;

import java.io.Serializable;

import de.retest.recheck.util.ChecksumCalculator;
import de.retest.recheck.util.ExceptionUtil;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ExceptionWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String errorId;

	@XmlAttribute
	private final String type;

	@XmlElement
	private final String stacktrace;

	@XmlElement
	private final String errorMsg;

	@XmlTransient
	private transient Throwable throwable;

	@XmlElement
	private final String errorLocation;

	@SuppressWarnings( "unused" )
	private ExceptionWrapper() {
		// for JAXB
		errorId = null;
		type = null;
		stacktrace = null;
		errorMsg = null;
		errorLocation = null;
	}

	public ExceptionWrapper( final Throwable throwable ) {
		assert throwable != null;
		this.throwable = throwable;
		final Throwable deepestCause = ExceptionUtil.getDeepestCause( throwable );
		type = deepestCause.getClass().getName();
		errorMsg = deepestCause.getMessage();
		errorLocation = ExceptionUtil.getDeepestStackTraceElement( deepestCause );
		errorId = ChecksumCalculator.getInstance().sha256( toString() );
		stacktrace = ExceptionUtil.getStackTrace( throwable );
	}

	public ExceptionWrapper( final String type, final String stacktrace, final String errorMsg,
			final String errorLocation ) {
		this.type = type;
		this.stacktrace = stacktrace;
		this.errorMsg = errorMsg;
		this.errorLocation = errorLocation;
		errorId = ChecksumCalculator.getInstance().sha256( toString() );
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public String getStackTrace() {
		return stacktrace;
	}

	public String getMessage() {
		return errorMsg;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		String result = type;
		if ( errorMsg != null && !errorMsg.trim().isEmpty() ) {
			result += ": " + errorMsg;
		}
		if ( errorLocation != null && !errorLocation.trim().isEmpty() ) {
			result += " at " + errorLocation;
		}
		return result;
	}

}
