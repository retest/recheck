package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.ui.diff.AttributesDifference;

public class AttributesDifferencePrinter implements Printer<AttributesDifference> {

	private final AttributeDifferencePrinter delegate;

	public AttributesDifferencePrinter( final PrinterValueProvider finder ) {
		delegate = new AttributeDifferencePrinter( finder );
	}

	@Override
	public String toString( final AttributesDifference difference, final String indent ) {
		return difference.getAttributes().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
