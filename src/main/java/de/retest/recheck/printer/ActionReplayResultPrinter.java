package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.NoRecheckFileActionReplayResult;
import de.retest.report.ActionReplayResult;
import de.retest.ui.DefaultValueFinder;
import de.retest.ui.actions.ExceptionWrapper;

public class ActionReplayResultPrinter implements Printer<ActionReplayResult> {

	private final ElementDifferencePrinter printer;

	public ActionReplayResultPrinter( final DefaultValueFinder defaultValueFinder ) {
		printer = new ElementDifferencePrinter( defaultValueFinder );
	}

	@Override
	public String toString( final ActionReplayResult difference, final String indent ) {
		final String prefix = indent + difference.getDescription() + " resulted in:\n";
		final ExceptionWrapper error = difference.getThrowableWrapper();
		if ( error != null ) {
			return prefix + indent + "\t" + error;
		}
		final Throwable targetNotFound = difference.getTargetNotFoundException();
		if ( targetNotFound != null ) {
			return prefix + indent + "\t" + targetNotFound;
		}
		if ( difference instanceof NoRecheckFileActionReplayResult ) {
			return prefix + indent + "\t" + difference.toStringDetailed();
		}
		final String diffs = difference.getElementDifferences().stream() //
				.map( elementDifference -> printer.toString( elementDifference, indent + "\t" ) ) //
				.collect( Collectors.joining( "\n" ) );
		return prefix + diffs;
	}

}
