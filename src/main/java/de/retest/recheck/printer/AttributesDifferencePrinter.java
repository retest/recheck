package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributesDifference;

public class AttributesDifferencePrinter implements Printer<AttributesDifference> {

	private final AttributeDifferencePrinter delegate;

	public AttributesDifferencePrinter( final IdentifyingAttributes attributes, final DefaultValueFinder finder ) {
		delegate = new AttributeDifferencePrinter( attributes, finder );
	}

	@Override
	public String toString( final AttributesDifference difference, final String indent ) {
		return difference.getDifferences().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
