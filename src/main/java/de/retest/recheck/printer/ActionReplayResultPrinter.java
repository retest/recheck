package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.NoRecheckFileActionReplayResult;
import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.actions.ExceptionWrapper;

public class ActionReplayResultPrinter implements Printer<ActionReplayResult> {

	private final ElementDifferencePrinter printer;
	private final ShouldIgnore ignore;

	public ActionReplayResultPrinter( final DefaultValueFinder defaultValueFinder, final ShouldIgnore ignore ) {
		printer = new ElementDifferencePrinter( defaultValueFinder, ignore );
		this.ignore = ignore;
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
			return prefix + indent + "\t" + NoRecheckFileActionReplayResult.MSG_LONG;
		}
		final String diffs = difference.getAllElementDifferences().stream() //
				.filter( diff -> !ignore.shouldIgnoreElement( diff.getElement() ) ) //
				.filter( diff -> !diff.getAttributeDifferences( ignore ).isEmpty() )
				.map( diff -> printer.toString( diff, indent + "\t" ) ) //
				.collect( Collectors.joining( "\n" ) );
		return prefix + diffs;
	}

}
