package de.retest.recheck.printer.leaf;

import de.retest.recheck.printer.Printer;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;

public class InsertedDeletedElementDifferencePrinter implements Printer<InsertedDeletedElementDifference> {

	@Override
	public String toString( final InsertedDeletedElementDifference difference, final String indent ) {
		return indent + format( difference );
	}

	private String format( final InsertedDeletedElementDifference difference ) {
		if ( difference.isInserted() ) {
			return "was inserted";
		}
		return "was deleted";
	}
}
