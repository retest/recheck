package de.retest.recheck.printer.leaf;

import java.util.HashMap;
import java.util.Map;

import de.retest.recheck.printer.Printer;
import de.retest.recheck.printer.PrinterValueProvider;
import de.retest.ui.diff.DurationDifference;
import de.retest.ui.diff.IdentifyingAttributesDifference;
import de.retest.ui.diff.InsertedDeletedElementDifference;
import de.retest.ui.diff.LeafDifference;

public class MultiLeafDifferencePrinter implements Printer<LeafDifference> {

	private final Map<Class<? extends LeafDifference>, Printer<? extends LeafDifference>> printers;

	public MultiLeafDifferencePrinter( final PrinterValueProvider defaultValueFinder ) {
		printers = createPrinterMap( defaultValueFinder );
	}

	private Map<Class<? extends LeafDifference>, Printer<? extends LeafDifference>>
			createPrinterMap( final PrinterValueProvider finder ) {
		final Map<Class<? extends LeafDifference>, Printer<? extends LeafDifference>> map = new HashMap<>();

		map.put( DurationDifference.class, new DurationDifferencePrinter() );
		map.put( IdentifyingAttributesDifference.class, new IdentifyingAttributesDifferencePrinter( finder ) );
		map.put( InsertedDeletedElementDifference.class, new InsertedDeletedElementDifferencePrinter() );

		return map;
	}

	@Override
	public String toString( final LeafDifference difference, final String indent ) {
		final Printer<LeafDifference> printer = findPrinter( difference );
		return printer.toString( difference, indent );
	}

	@SuppressWarnings( "unchecked" )
	private Printer<LeafDifference> findPrinter( final LeafDifference difference ) {
		return (Printer<LeafDifference>) printers.getOrDefault( difference.getClass(), DefaultPrinter.INSTANCE );
	}

	private enum DefaultPrinter implements Printer<LeafDifference> {
		INSTANCE;

		@Override
		public String toString( final LeafDifference difference, final String indent ) {
			return indent + String.format( "expected=%s, actual=%s", difference.getExpected(), difference.getActual() );
		}
	}
}
