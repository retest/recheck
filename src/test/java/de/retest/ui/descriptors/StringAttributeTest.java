package de.retest.ui.descriptors;

import static de.retest.ui.descriptors.StringAttribute.parameterTypeBoolean;
import static de.retest.ui.descriptors.StringAttribute.parameterTypeClass;
import static de.retest.ui.descriptors.StringAttribute.parameterTypeInteger;
import static de.retest.ui.descriptors.StringAttribute.parameterTypeString;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StringAttributeTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void should_parse_Boolean_values_correctly() throws Exception {
		assertThat( (Boolean) parameterTypeBoolean.parse( "TRUE" ) ).isTrue();
		assertThat( (Boolean) parameterTypeBoolean.parse( "True" ) ).isTrue();
		assertThat( (Boolean) parameterTypeBoolean.parse( "true" ) ).isTrue();
		assertThat( (Boolean) parameterTypeBoolean.parse( "TrUe" ) ).isTrue();
		assertThat( (Boolean) parameterTypeBoolean.parse( "FALSE" ) ).isFalse();
		assertThat( (Boolean) parameterTypeBoolean.parse( "False" ) ).isFalse();
		assertThat( (Boolean) parameterTypeBoolean.parse( "false" ) ).isFalse();
		assertThat( (Boolean) parameterTypeBoolean.parse( "fAlSe" ) ).isFalse();
	}

	@Test
	public void parse_should_throw_error_if_not_valid() throws Exception {
		exception.expect( ParameterParseException.class );
		exception.expectMessage( "Value must be 'true' or 'false' (ignoring case)." );

		parameterTypeBoolean.parse( "not a boolean value" );
	}

	@Test
	public void should_parse_Class_values_correctly() throws Exception {
		assertThat( parameterTypeClass.parse( "java.lang.Integer" ) ).isEqualTo( Integer.class );
		assertThat( parameterTypeClass.parse( "de.retest.ui.descriptors.StringAttributeTest" ) )
				.isEqualTo( getClass() );
	}

	@Test( expected = ParameterParseException.class )
	public void parse_should_throw_class_not_found() throws Exception {
		parameterTypeClass.parse( "this.is.a.non.existent.Class" );
	}

	@Test
	public void should_return_String_values_as_is() throws Exception {
		assertThat( parameterTypeString.parse( "Hi" ) ).isEqualTo( "Hi" );
		assertThat( parameterTypeString.parse( "42" ) ).isEqualTo( "42" );
		assertThat( parameterTypeString.parse( "true" ) ).isEqualTo( "true" );
	}

	@Test
	public void should_parse_Integer_correctly() throws Exception {
		assertThat( parameterTypeInteger.parse( "42" ) ).isEqualTo( 42 );
		assertThat( parameterTypeInteger.parse( "4711" ) ).isEqualTo( 4711 );
		assertThat( parameterTypeInteger.parse( "0815" ) ).isEqualTo( 815 );
	}

	@Test( expected = ParameterParseException.class )
	public void parse_should_throw_exception() throws Exception {
		parameterTypeInteger.parse( "test" );
	}
}
