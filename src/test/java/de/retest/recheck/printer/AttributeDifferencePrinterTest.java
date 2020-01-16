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
import de.retest.recheck.ui.diff.ElementIdentificationWarning;

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

	@Test
	void to_String_should_identify_actual_default_and_attach_warning() {
		final AttributeDifference difference = new AttributeDifference( "a", "b", "a" );
		final ElementIdentificationWarning warning =
				new ElementIdentificationWarning( "TestClassIT.java", 31, "id", "de.example.web.TestClassIT" );
		difference.addElementIdentificationWarning( warning );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "actual=\"(default or absent)\"" ).contains( "breaks=\"TestClassIT.java:31\"" );
	}

	@Test
	void to_String_should_expect_expected_default_when_null_and_attach_warning() {
		final AttributeDifference difference = new AttributeDifference( "a", null, "a" );
		final ElementIdentificationWarning warning =
				new ElementIdentificationWarning( "FooIT.java", 2, "id", "de.example.web.FooIT" );
		difference.addElementIdentificationWarning( warning );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "expected=\"(default or absent)\"" ).contains( "breaks=\"FooIT.java:2\"" );
	}

	@Test
	void to_String_should_attach_amount_of_warnings() {
		final AttributeDifference difference = new AttributeDifference( "a", "a", "b" );
		final ElementIdentificationWarning warning1 =
				new ElementIdentificationWarning( "FooIT.java", 2, "id", "de.example.web.FooIT" );
		final ElementIdentificationWarning warning2 =
				new ElementIdentificationWarning( "BarIT.java", 3, "id", "de.example.web.BarIT" );
		difference.addElementIdentificationWarning( warning1 );
		difference.addElementIdentificationWarning( warning2 );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "breaks=\"FooIT.java:2, BarIT.java:3\"" );
	}

	class PrinterFinder implements DefaultValueFinder {

		@Override
		public boolean isDefaultValue( final IdentifyingAttributes attributes, final String key,
				final Serializable value ) {
			return Objects.equals( "a", value );
		}
	}
}
