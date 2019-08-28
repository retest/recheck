package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.LeafDifference;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ElementDifferencePrinter implements Printer<ElementDifference> {

	private final DefaultValueFinder finder;

	@Override
	public String toString( final ElementDifference difference, final String indent ) {
		return indent + createDescription( difference ) + "\n" + createDifferences( difference, indent + "\t" );
	}

	private String createDescription( final ElementDifference difference ) {
		final IdentifyingAttributes attributes = difference.getIdentifyingAttributes();
		return attributes + " at '" + attributes.getPath() + "':";
	}

	private String createDifferences( final ElementDifference difference, final String indent ) {
		final LeafDifference identifyingAttributesDifference = difference.getIdentifyingAttributesDifference();
		if ( identifyingAttributesDifference instanceof InsertedDeletedElementDifference ) {
			final Printer<InsertedDeletedElementDifference> printer = new InsertedDeletedElementDifferencePrinter();
			return printer.toString( (InsertedDeletedElementDifference) identifyingAttributesDifference, indent );
		}
		final IdentifyingAttributes attributes = difference.getIdentifyingAttributes();
		final AttributeDifferencePrinter delegate = new AttributeDifferencePrinter( attributes, finder );
		return difference.getAttributeDifferences().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
