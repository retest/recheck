package de.retest.recheck.persistence;

import java.net.URI;

import de.retest.recheck.ioerror.ReTestLoadException;

public class IncompatibleReportVersionException extends ReTestLoadException {

	private static final long serialVersionUID = 1L;

	private final String writerVersion;
	private final String readerVersion;

	public IncompatibleReportVersionException( final String writerVersion, final String readerVersion,
			final URI location, final Throwable throwable ) {
		super( location, "Incompatible retest versions: report was written with " + writerVersion + ", but read with "
				+ readerVersion + ".", throwable );
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
