package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.ElementDifference;

public class ElementDifferencePrinter implements Printer<ElementDifference> {

	private final DefaultValueFinder finder;
	private final Filter ignore;

	public ElementDifferencePrinter( final DefaultValueFinder finder, final Filter ignore ) {
		this.finder = finder;
		this.ignore = ignore;
	}

	@Override
	public String toString( final ElementDifference difference, final String indent ) {
		return indent + createDescription( difference ) + "\n" + createDifferences( difference, indent + "\t" );
	}

	private String createDescription( final ElementDifference difference ) {
		final IdentifyingAttributes attributes = difference.getIdentifyingAttributes();
		return attributes + " at '" + attributes.getPath() + "':";
	}

	private String createDifferences( final ElementDifference difference, final String indent ) {
		final IdentifyingAttributes attributes = difference.getIdentifyingAttributes();
		final AttributeDifferencePrinter delegate = new AttributeDifferencePrinter( attributes, finder );
		return difference.getAttributeDifferences( ignore ).stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
