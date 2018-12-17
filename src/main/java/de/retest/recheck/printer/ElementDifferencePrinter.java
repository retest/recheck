package de.retest.recheck.printer;

import java.util.List;
import java.util.stream.Collectors;

import de.retest.recheck.printer.leaf.MultiLeafDifferencePrinter;
import de.retest.ui.DefaultValueFinder;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.diff.AttributesDifference;
import de.retest.ui.diff.ElementDifference;
import de.retest.ui.diff.LeafDifference;

public class ElementDifferencePrinter implements Printer<ElementDifference> {

	private final DefaultValueFinder finder;

	public ElementDifferencePrinter( final DefaultValueFinder finder ) {
		this.finder = finder;
	}

	@Override
	public String toString( final ElementDifference difference, final String indent ) {
		final List<ElementDifference> elementDifferences = difference.getElementDifferences();
		return elementDifferences.stream() //
				.limit( 50 ) //
				.map( e -> formatElementDifference( e, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

	private String formatElementDifference( final ElementDifference difference, final String indent ) {
		final IdentifyingAttributes id = difference.getIdentifyingAttributes();
		final String prefix = String.format( "%s%s at '%s':%n", indent, id, id.getPath() );
		return prefix + to( difference, indent + "\t" );
	}

	private String to( final ElementDifference difference, final String indent ) {
		final PrinterValueProvider defaults = PrinterValueProvider.of( finder, difference.getIdentifyingAttributes() );
		final LeafDifference identifyingAttributesDifference = difference.getIdentifyingAttributesDifference();
		if ( identifyingAttributesDifference != null ) {
			final MultiLeafDifferencePrinter printer = new MultiLeafDifferencePrinter( defaults );
			return printer.toString( identifyingAttributesDifference, indent );
		}
		final AttributesDifference attributesDifference = difference.getAttributesDifference();
		if ( attributesDifference != null ) {
			final AttributesDifferencePrinter printer = new AttributesDifferencePrinter( defaults );
			return printer.toString( attributesDifference, indent );
		}
		return indent + "noDifferences";
	}
}
