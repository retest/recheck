package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

class AttributeDifferencePrinterTest {

	AttributeDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		final IdentifyingAttributes attributes = mock( IdentifyingAttributes.class );
		cut = new AttributeDifferencePrinter( attributes, new PrinterFinder() );
	}

	@Test
	void toString_should_identify_actual_default() {
		final AttributeDifference d1 = new AttributeDifference( "a", "b", "a" );

		final String string = cut.toString( d1 );

		assertThat( string ).contains( "actual=\"(default or absent)\"" );
	}

	@Test
	void toString_should_not_identify_actual_default() {
		final AttributeDifference d1 = new AttributeDifference( "a", "b", "b" );

		final String string = cut.toString( d1 );

		assertThat( string ).doesNotContain( "actual=\"(default or absent)\"" );
	}

	@Test
	void to_String_should_expect_expected_default_when_null() {
		final AttributeDifference difference = new AttributeDifference( "a", null, "b" );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "expected=\"(default or absent)\"" );
	}

	@Test
	void to_String_should_take_expected_priority_when_actual_default() {
		final AttributeDifference difference = new AttributeDifference( "a", null, "a" );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "expected=\"(default or absent)\"" );
	}

	class PrinterFinder implements DefaultValueFinder {

		@Override
		public boolean isDefaultValue( final IdentifyingAttributes attributes, final String key,
				final Serializable value ) {
			return Objects.equals( "a", value );
		}
	}
}
