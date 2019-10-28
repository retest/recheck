package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.netmikey.logunit.api.LogCapturer;

class AttributeDifferenceTest {

	@RegisterExtension
	LogCapturer logs = LogCapturer.create().captureForType( AttributeDifference.class );

	@Test
	void apply_changes_to_null_attribute_yields_NullPointerException() throws Exception {
		final AttributeDifference cut = new AttributeDifference();
		assertThatThrownBy( () -> cut.applyChangeTo( null ) ) //
				.isInstanceOf( NullPointerException.class ) //
				.hasMessage( "Cannot apply change to an attribute that is null." );
	}

	@Test
	void compare_to_same_object_is_zero() throws Exception {
		final AttributeDifference cut = new AttributeDifference();
		assertThat( cut.compareTo( cut ) ).isEqualTo( 0 );
	}

	@ParameterizedTest
	@MethodSource( "warnArgs" )
	void should_warn_if_attributes_dont_match( final String expected, final String fromAttribute ) throws Exception {
		final String key = "key";
		final AttributeDifference cut = new AttributeDifference( key, expected, null );

		cut.warnIfAttributesDontMatch( fromAttribute );

		final String log = "Mismatch for attribute '" + key + "': value from ExecSuite '" + fromAttribute + "', "
				+ "value from TestResult '" + expected
				+ "'. This could be due to a change of the execsuite in between.";

		logs.assertContains( log );
	}

	static Stream<Arguments> warnArgs() {
		return Stream.of( //
				Arguments.of( "something", null ), //
				Arguments.of( "something", "" ), //
				Arguments.of( null, "somethingElse" ), //
				Arguments.of( "", "somethingElse" ), //
				Arguments.of( "something", "somethingElse" ) );
	}

	@ParameterizedTest
	@MethodSource( "dontWarnArgs" )
	void should_not_warn_if_attributes_are_null_or_empty_or_match( final String expected, final String fromAttribute )
			throws Exception {
		final String key = "key";
		final AttributeDifference cut = new AttributeDifference( key, expected, null );

		cut.warnIfAttributesDontMatch( fromAttribute );

		assertThat( logs.getEvents() ).isEmpty();
	}

	static Stream<Arguments> dontWarnArgs() {
		return Stream.of( //
				Arguments.of( null, null ), //
				Arguments.of( "", null ), //
				Arguments.of( null, "" ), //
				Arguments.of( "", "" ), //
				Arguments.of( "unchanged", "unchanged" ) );
	}
}
