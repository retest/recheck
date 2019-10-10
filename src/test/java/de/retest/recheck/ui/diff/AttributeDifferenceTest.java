package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class AttributeDifferenceTest {

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	@Test
	public void apply_changes_to_null_attribute_yields_NullPointerException() throws Exception {
		final AttributeDifference cut = new AttributeDifference();
		assertThatThrownBy( () -> cut.applyChangeTo( null ) ) //
				.isInstanceOf( NullPointerException.class ) //
				.hasMessage( "Cannot apply change to an attribute that is null." );
	}

	@Test
	public void compare_to_same_object_is_zero() throws Exception {
		final AttributeDifference cut = new AttributeDifference();
		assertThat( cut.compareTo( cut ) ).isEqualTo( 0 );
	}

	@Test
	public void warn_if_attributes_dont_match_logs_correct_string() throws Exception {
		final AttributeDifference cut = new AttributeDifference( null, "something", null );
		cut.warnIfAttributesDontMatch( "somethingElse" );

		final String expected = "Mismatch for attribute 'null': value from ExecSuite 'somethingElse', "
				+ "value from TestResult 'something'. This could be due to a change of the execsuite in between.";

		assertThat( systemOutRule.getLog() ).contains( expected );
	}
}
