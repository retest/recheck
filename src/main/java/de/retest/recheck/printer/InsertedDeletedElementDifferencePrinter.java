package de.retest.recheck.printer;

import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;

public class InsertedDeletedElementDifferencePrinter implements Printer<InsertedDeletedElementDifference> {

	@Override
	public String toString( final InsertedDeletedElementDifference difference, final String indent ) {
		return indent + (difference.isInserted() ? "was inserted" : "was deleted");
	}
}
