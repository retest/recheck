package de.retest.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import de.retest.ui.diff.AttributeDifference;

class AttributeDifferenceTest {

	@Test
	void apply_changes_to_null_attribute_yields_NullPointerException() {
		final AttributeDifference cut = new AttributeDifference();
		assertThatThrownBy( () -> cut.applyChangeTo( null ) ) //
				.isInstanceOf( NullPointerException.class ) //
				.hasMessage( "Cannot apply change to an attribute that is null." );
	}

	@Test
	void equals_should_be_false_for_null() {
		final AttributeDifference cut = new AttributeDifference();
		assertThat( cut.equals( null ) ).isFalse();
	}

	@Test
	void equals_should_be_false_for_other_types() {
		final AttributeDifference cut = new AttributeDifference();
		assertThat( cut.equals( new Object() ) ).isFalse();
	}

	@Test
	void equals_should_be_true_if_same_object() {
		final AttributeDifference cut = new AttributeDifference();
		assertThat( cut.equals( cut ) ).isTrue();
	}

	@Test
	void equals_should_be_true_if_same_properties() {
		final String key = "key";
		final String expected = "foo";
		final String actual = "bar";
		final AttributeDifference cut0 = new AttributeDifference( key, expected, actual );
		final AttributeDifference cut1 = new AttributeDifference( key, expected, actual );
		assertThat( cut0.equals( cut1 ) ).isTrue();
	}

	@Test
	void equals_should_be_false_if_not_same_properties() {
		final String key = "key";
		final String expected = "foo";
		final String actual = "bar";
		final AttributeDifference cut0 = new AttributeDifference( key, expected, actual );
		final AttributeDifference cut1 = new AttributeDifference( key, expected, "baz" );
		assertThat( cut0.equals( cut1 ) ).isFalse();
	}
}
