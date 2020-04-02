package de.retest.recheck.printer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.printer.highlighting.DefaultHighlighter;
import de.retest.recheck.printer.highlighting.Highlighter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.ui.diff.meta.MetadataDifference;

public class ActionReplayResultPrinter implements Printer<ActionReplayResult> {

	private final ElementDifferencePrinter printer;
	private final MetadataDifferencePrinter metadataDifferencePrinter;

	public ActionReplayResultPrinter( final DefaultValueFinder defaultValueFinder ) {
		printer = new ElementDifferencePrinter( defaultValueFinder, new DefaultHighlighter() );
		metadataDifferencePrinter = new MetadataDifferencePrinter();
	}

	public ActionReplayResultPrinter( final DefaultValueFinder defaultValueFinder, final Highlighter highlighter ) {
		printer = new ElementDifferencePrinter( defaultValueFinder, highlighter );
		metadataDifferencePrinter = new MetadataDifferencePrinter( highlighter );
	}

	@Override
	public String toString( final ActionReplayResult difference, final String indent ) {
		final String prefix = indent + createDescription( difference ) + "\n";
		final String nextIndent = indent + "\t";
		if ( difference instanceof NoGoldenMasterActionReplayResult ) {
			return prefix + nextIndent + NoGoldenMasterActionReplayResult.MSG_LONG;
		}
		final StateDifference stateDifference = difference.getStateDifference();
		final boolean hasStateDifferences = difference.hasDifferences();
		final MetadataDifference metadataDifference = difference.getMetadataDifference();
		final boolean hasMetadataDifferences = !metadataDifference.isEmpty();

		if ( !hasMetadataDifferences && hasStateDifferences ) {
			return prefix + createStateDifferences( stateDifference, nextIndent );
		}
		if ( hasMetadataDifferences && !hasStateDifferences ) {
			return prefix + createMetadataDifferences( metadataDifference, nextIndent );
		}
		return prefix // 
				+ createMetadataDifferences( metadataDifference, nextIndent ) + "\n" // 
				+ createStateDifferences( stateDifference, nextIndent );
	}

	private String createDescription( final ActionReplayResult difference ) {
		return difference.getDescription() + " resulted in:";
	}

	private String createMetadataDifferences( final MetadataDifference difference, final String indent ) {
		return metadataDifferencePrinter.toString( difference, indent );
	}

	private String createStateDifferences( final StateDifference difference, final String indent ) {
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
