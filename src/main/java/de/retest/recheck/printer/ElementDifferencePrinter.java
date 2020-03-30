package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.printer.highlighting.DefaultHighlighter;
import de.retest.recheck.printer.highlighting.Highlighter;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.LeafDifference;

public class ElementDifferencePrinter implements Printer<ElementDifference> {

	private final DefaultValueFinder finder;
	private final Highlighter highlighter;

	public ElementDifferencePrinter( final DefaultValueFinder finder ) {
		this.finder = finder;
		highlighter = new DefaultHighlighter();
	}

	public ElementDifferencePrinter( final DefaultValueFinder finder, final Highlighter highlighter ) {
		this.finder = finder;
		this.highlighter = highlighter;
	}

	@Override
	public String toString( final ElementDifference difference, final String indent ) {
		return indent + createDescription( difference ) + "\n" + createDifferences( difference, indent + "\t" );
	}

	private String createDescription( final ElementDifference difference ) {
		final IdentifyingAttributes attributes = difference.getIdentifyingAttributes();
		return attributes.getType() + " (" + difference.getElement() + ") at '" + attributes.getPath() + "':";
	}

	private String createDifferences( final ElementDifference difference, final String indent ) {
		final LeafDifference identifyingAttributesDifference = difference.getIdentifyingAttributesDifference();
		if ( identifyingAttributesDifference instanceof InsertedDeletedElementDifference ) {
			final Printer<InsertedDeletedElementDifference> printer = new InsertedDeletedElementDifferencePrinter();
			return printer.toString( (InsertedDeletedElementDifference) identifyingAttributesDifference, indent );
		}
		final IdentifyingAttributes attributes = difference.getIdentifyingAttributes();
		final AttributeDifferencePrinter delegate = new AttributeDifferencePrinter( attributes, finder, highlighter );
		return difference.getAttributeDifferences().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
