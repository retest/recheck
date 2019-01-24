package de.retest.recheck.printer.leaf;

import java.util.stream.Collectors;

import de.retest.recheck.printer.AttributeDifferencePrinter;
import de.retest.recheck.printer.Printer;
import de.retest.recheck.printer.PrinterValueProvider;
import de.retest.ui.diff.IdentifyingAttributesDifference;

public class IdentifyingAttributesDifferencePrinter implements Printer<IdentifyingAttributesDifference> {

	private final AttributeDifferencePrinter delegate;

	public IdentifyingAttributesDifferencePrinter( final PrinterValueProvider finder ) {
		delegate = new AttributeDifferencePrinter( finder );
	}

	@Override
	public String toString( final IdentifyingAttributesDifference difference, final String indent ) {
		return difference.getAttributes().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
