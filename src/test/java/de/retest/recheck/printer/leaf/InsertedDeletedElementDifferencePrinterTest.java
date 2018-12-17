package de.retest.recheck.printer.leaf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.ui.diff.InsertedDeletedElementDifference;

class InsertedDeletedElementDifferencePrinterTest {

	InsertedDeletedElementDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		cut = new InsertedDeletedElementDifferencePrinter();
	}

	@Test
	void toString_should_identify_inserted() {
		final InsertedDeletedElementDifference difference = mock( InsertedDeletedElementDifference.class );
		when( difference.isInserted() ).thenReturn( true );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "was inserted" );
	}

	@Test
	void toString_should_identify_deleted() {
		final InsertedDeletedElementDifference difference = mock( InsertedDeletedElementDifference.class );
		when( difference.isInserted() ).thenReturn( false );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "was deleted" );
	}

	@Test
	void toString_should_respect_indent_on_insert() {
		final InsertedDeletedElementDifference difference = mock( InsertedDeletedElementDifference.class );
		when( difference.isInserted() ).thenReturn( true );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).startsWith( "____" );
	}

	@Test
	void toString_should_respect_indent_on_delete() {
		final InsertedDeletedElementDifference difference = mock( InsertedDeletedElementDifference.class );
		when( difference.isInserted() ).thenReturn( false );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).startsWith( "____" );
	}
}
