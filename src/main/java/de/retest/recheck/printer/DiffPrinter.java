package de.retest.recheck.printer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.persistence.Persistence;
import de.retest.persistence.bin.KryoPersistence;
import de.retest.report.ReplayResult;

public class DiffPrinter {

	private static final Logger logger = LoggerFactory.getLogger( DiffPrinter.class );

	private final List<String> checks;
	private final PrintStream out;

	public DiffPrinter( final PrintStream out ) {
		this( null, out );
	}

	public DiffPrinter( final List<String> checks, final PrintStream out ) {
		this.checks = checks == null ? new ArrayList<>() : checks;
		this.out = out;
	}

	public void printDiff( final File latestReport ) {
		try {
			final ReplayResult report = loadReport( latestReport );
			out.println( generateFilteredDiffString( report ) );
		} catch ( final Exception e ) {
			logger.error( "Exception printing Diff of report {}:", latestReport, e );
		}
	}

	public String generateFilteredDiffString( final ReplayResult report ) {
		final StringBuilder result = new StringBuilder();
		report.getSuiteReplayResults().stream() //
				.flatMap( suiteResult -> suiteResult.getTestReplayResults().stream() ) //
				.flatMap( testResult -> testResult.getActionReplayResults().stream() ) //
				.forEach( actionReplayResult -> {
					if ( checks.isEmpty() || checks.contains( actionReplayResult.getDescription() ) ) {
						result.append( actionReplayResult.toStringDetailed() ).append( "\n" );
					}
				} );
		return result.toString();
	}

	private ReplayResult loadReport( final File latestReport ) throws IOException {
		final Persistence<ReplayResult> resultPersistence = new KryoPersistence<>();
		return resultPersistence.load( latestReport.toURI() );
	}
}
