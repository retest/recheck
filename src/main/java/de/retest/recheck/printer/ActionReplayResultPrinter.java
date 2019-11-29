package de.retest.recheck.printer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;

public class ActionReplayResultPrinter implements Printer<ActionReplayResult> {

	private final ElementDifferencePrinter printer;

	public ActionReplayResultPrinter( final DefaultValueFinder defaultValueFinder ) {
		printer = new ElementDifferencePrinter( defaultValueFinder );
	}

	@Override
	public String toString( final ActionReplayResult difference, final String indent ) {
		final String prefix = indent + createDescription( difference ) + "\n";
		final String nextIndent = indent + "\t";

		if ( difference instanceof NoGoldenMasterActionReplayResult ) {
			return prefix + nextIndent + NoGoldenMasterActionReplayResult.MSG_LONG;
		}
		return prefix + createDifferences( difference.getStateDifference(), nextIndent );
	}

	private String createDescription( final ActionReplayResult difference ) {
		return difference.getDescription() + " resulted in:";
	}

	private String createDifferences( final StateDifference difference, final String indent ) {
		return difference.getRootElementDifferences().stream() //
				.map( RootElementDifference::getElementDifference ) //
				.flatMap( this::getDifference ) //
				.filter( ElementDifference::hasAnyDifference ) //
				.map( diff -> printer.toString( diff, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

	private Stream<ElementDifference> getDifference( final ElementDifference origin ) {
		if ( origin.isInsertionOrDeletion() ) { // Do not traverse deeper, since those should only be inserted or deleted
			return Stream.of( origin );
		}
		return Stream.concat( // 
				Stream.of( origin ), // 
				origin.getChildDifferences().stream() //
						.flatMap( this::getDifference ) );
	}
}
