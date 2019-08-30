package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.actions.ExceptionWrapper;
import de.retest.recheck.ui.diff.ElementDifference;

public class ActionReplayResultPrinter implements Printer<ActionReplayResult> {

	private final ElementDifferencePrinter printer;

	public ActionReplayResultPrinter( final DefaultValueFinder defaultValueFinder ) {
		printer = new ElementDifferencePrinter( defaultValueFinder );
	}

	@Override
	public String toString( final ActionReplayResult difference, final String indent ) {
		final String prefix = indent + createDescription( difference ) + "\n";
		final String nextIndent = indent + "\t";
		final ExceptionWrapper error = difference.getThrowableWrapper();
		if ( error != null ) {
			return prefix + nextIndent + error;
		}
		final Throwable targetNotFound = difference.getTargetNotFoundException();
		if ( targetNotFound != null ) {
			return prefix + nextIndent + targetNotFound;
		}
		if ( difference instanceof NoGoldenMasterActionReplayResult ) {
			return prefix + nextIndent + NoGoldenMasterActionReplayResult.MSG_LONG;
		}
		return prefix + createDifferences( difference, nextIndent );
	}

	private String createDescription( final ActionReplayResult difference ) {
		return difference.getDescription() + " resulted in:";
	}

	private String createDifferences( final ActionReplayResult difference, final String indent ) {
		return difference.getAllElementDifferences().stream() //
				.filter( ElementDifference::hasAnyDifference ) //
				.map( diff -> printer.toString( diff, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
