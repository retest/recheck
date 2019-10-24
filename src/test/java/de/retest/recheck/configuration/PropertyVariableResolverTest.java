package de.retest.recheck.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

public class PropertyVariableResolverTest {

	Properties origin;
	PropertyVariableResolver resolved;

	@Before
	public void setUp() throws Exception {
		origin = new Properties();
		resolved = new PropertyVariableResolver( origin );
	}

	@Test
	public void simple_test() {
		origin.setProperty( "key", "value" );
		assertThat( resolved.getProperty( "key" ) ).isEqualTo( "value" );
	}

	@Test
	public void simple_test_with_default() {
		origin.setProperty( "key", "value" );

		assertThat( resolved.getProperty( "key", "default" ) ).isEqualTo( "value" );
		assertThat( resolved.getProperty( "nokey", "default" ) ).isEqualTo( "default" );
	}

	@Test
	public void non_retest_values_should_not_be_replaced() {
		origin.setProperty( "varName", "varValue" );
		origin.setProperty( "key", "${varName}" );

		assertThat( resolved.getProperty( "key" ) ).isEqualTo( "${varName}" );
	}

	@Test
	public void replaces_complete_value() {
		origin.setProperty( "varName", "varValue" );
		origin.setProperty( "de.retest.key", "${varName}" );

		assertThat( resolved.getProperty( "de.retest.key" ) ).isEqualTo( "varValue" );
	}

	@Test
	public void no_replacement_should_resolve_to_key() {
		origin.setProperty( "de.retest.key", "${varName}" );

		assertThat( resolved.getProperty( "de.retest.key" ) ).isEqualTo( "varName" );
	}

	@Test
	public void replaces_partly_values() {
		origin.setProperty( "varName", "varValue" );
		origin.setProperty( "de.retest.prefixkey", "foo${varName}" );
		origin.setProperty( "de.retest.keysuffix", "${varName}bar" );
		origin.setProperty( "de.retest.key", "foo${varName}bar" );

		assertThat( resolved.getProperty( "de.retest.prefixkey" ) ).isEqualTo( "foovarValue" );
		assertThat( resolved.getProperty( "de.retest.keysuffix" ) ).isEqualTo( "varValuebar" );
		assertThat( resolved.getProperty( "de.retest.key" ) ).isEqualTo( "foovarValuebar" );
	}

	@Test
	public void replaces_keys_with_special_chars() {
		origin.setProperty( "name.with_spcial.chars", "varValue" );
		origin.setProperty( "de.retest.key", "${name.with_spcial.chars}" );

		assertThat( resolved.getProperty( "de.retest.key" ) ).isEqualTo( "varValue" );
	}

	@Test
	public void replaces_keys_with_special_chars_on_irrelevant_positions() {
		origin.setProperty( "varName", "varValue" );
		origin.setProperty( "de.retest.prefixkey", "f.o_o${varName}" );
		origin.setProperty( "de.retest.keysuffix", "${varName}b.a_r" );
		origin.setProperty( "de.retest.key", "fo_o${varName}ba.r" );

		assertThat( resolved.getProperty( "de.retest.prefixkey" ) ).isEqualTo( "f.o_ovarValue" );
		assertThat( resolved.getProperty( "de.retest.keysuffix" ) ).isEqualTo( "varValueb.a_r" );
		assertThat( resolved.getProperty( "de.retest.key" ) ).isEqualTo( "fo_ovarValueba.r" );
	}

	@Test
	public void replaces_dollar_and_brackets_correctly() {
		origin.setProperty( "varName", "varValue" );
		origin.setProperty( "de.retest.prefixkey", "f${oo${varName}" );
		origin.setProperty( "de.retest.keysuffix", "${varName}ba}r" );

		assertThat( resolved.getProperty( "de.retest.prefixkey" ) ).isEqualTo( "f${oovarValue" );
		assertThat( resolved.getProperty( "de.retest.keysuffix" ) ).isEqualTo( "varValueba}r" );
	}

	@Test
	public void replaces_inner_variables_correctly() {
		origin.setProperty( "innerVarName", "varValue" );
		origin.setProperty( "outerVarName", "${innerVarName}" );
		origin.setProperty( "de.retest.key", "${outerVarName}" );

		assertThat( resolved.getProperty( "de.retest.key" ) ).isEqualTo( "varValue" );
	}

	@Test
	public void replaces_mixed_variables_correctly() {
		origin.setProperty( "innerVarName", "VarName" );
		origin.setProperty( "outerVarName", "varValue" );
		origin.setProperty( "de.retest.key", "${outer${innerVarName}}" );

		assertThat( resolved.getProperty( "de.retest.key" ) ).isEqualTo( "varValue" );
	}

	@Test
	public void ignored_keys_should_not_be_resolved() throws Exception {
		final SoftAssertions softly = new SoftAssertions();
		for ( final String ignoredKey : PropertyVariableResolver.IGNORED_KEYS ) {
			final String someIgnoredProperty = ignoredKey + ".someIgnoredProperty";
			softly.assertThat( resolved.getProperty( someIgnoredProperty ) ).isNull();
		}
		softly.assertAll();
	}

}
