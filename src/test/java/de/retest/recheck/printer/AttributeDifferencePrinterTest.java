package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.ui.descriptors.AttributeDifference;

class AttributeDifferencePrinterTest {

	AttributeDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		cut = new AttributeDifferencePrinter( new PrinterFinder() );
	}

	@Test
	void toString_should_identify_actual_default() {
		final AttributeDifference d1 = new AttributeDifference( "a", "b", "a" );

		final String string = cut.toString( d1 );

		assertThat( string ).contains( "actual=\"(default)\"" );
	}

	@Test
	void toString_should_not_identify_actual_default() {
		final AttributeDifference d1 = new AttributeDifference( "a", "b", "b" );

		final String string = cut.toString( d1 );

		assertThat( string ).doesNotContain( "actual=\"(default)\"" );
	}

	@Test
	void to_String_should_expect_expected_default_when_null() {
		final AttributeDifference difference = new AttributeDifference( "a", null, "b" );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "expected=\"(default)\"" );
	}

	@Test
	void to_String_should_take_expected_priority_when_actual_default() {
		final AttributeDifference difference = new AttributeDifference( "a", null, "a" );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "expected=\"(default)\"" );
	}

	class PrinterFinder implements PrinterValueProvider {

		@Override
		public boolean isDefault( final String key, final Serializable value ) {
			return Objects.equals( "a", value );
		}
	}
}
