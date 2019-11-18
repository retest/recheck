package de.retest.recheck.persistence;

import java.net.URI;

import de.retest.recheck.ioerror.ReTestLoadException;

public class IncompatibleReportVersionException extends ReTestLoadException {

	private static final String MESSAGE =
			"Incompatible recheck versions: report was written with %s, but read with %s.";

	private static final long serialVersionUID = 1L;

	private final String writerVersion;
	private final String readerVersion;

	public IncompatibleReportVersionException( final String writerVersion, final String readerVersion,
			final URI location ) {
		super( location, String.format( MESSAGE, writerVersion, readerVersion ) );
		this.writerVersion = writerVersion;
		this.readerVersion = readerVersion;
	}

	public IncompatibleReportVersionException( final String writerVersion, final String readerVersion,
			final URI location, final Throwable throwable ) {
		super( location, String.format( MESSAGE, writerVersion, readerVersion ), throwable );
		this.writerVersion = writerVersion;
		this.readerVersion = readerVersion;
	}

	public String getWriterVersion() {
		return writerVersion;
	}

	public String getReaderVersion() {
		return readerVersion;
	}
}
