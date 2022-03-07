package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Rectangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class PixelDiffFilterTest {

	double pixelDiff = 5.0;
	Filter cut;
	Element element;

	@BeforeEach
	void setUp() {
		cut = new PixelDiffFilter( "5.0", pixelDiff );
		element = mock( Element.class );
	}

	@Test
	void should_filter_diff_when_pixel_diff_is_not_exceeded() throws Exception {
		final Rectangle expected = new Rectangle( 0, 0, 10, 10 );
		final Rectangle actual = new Rectangle( 1, -1, 15, 5 );
		final AttributeDifference diff = new AttributeDifference( "outline", expected, actual );

		assertThat( cut.matches( element, diff ) ).isTrue();
	}

	@Test
	void should_not_filter_diff_when_pixel_diff_is_exceeded() throws Exception {
		final Rectangle expected = new Rectangle( 0, 0, 10, 10 );
		final Rectangle actual = new Rectangle( 1, -1, 15, 5 );
		final AttributeDifference diff = new AttributeDifference( "outline", expected, actual );

		final Filter cut = new PixelDiffFilter( "0", 0.0 );

		assertThat( cut.matches( element, diff ) ).isFalse();
	}

	@Test
	void should_handle_pixel_strings_with_integers_and_floats() throws Exception {
		final String expected = "50px";
		final String actual = "45.3px";
		final AttributeDifference diff = new AttributeDifference( "foo", expected, actual );

		assertThat( cut.matches( element, diff ) ).isTrue();
	}

	@Test
	void should_handle_pixel_strings_with_negative_integers_and_floats() throws Exception {
		final String expected = "-50px";
		final String actual = "-45.3px";
		final AttributeDifference diff = new AttributeDifference( "foo", expected, actual );

		assertThat( cut.matches( element, diff ) ).isTrue();
	}

	@Test
	void should_handle_pixel_strings_with_different_decimal_separators() throws Exception {
		final String expected = "50.0px";
		final String actual = "45,3px";
		final AttributeDifference diff = new AttributeDifference( "foo", expected, actual );

		assertThat( cut.matches( element, diff ) ).isTrue();
	}

	@Test
	void should_skip_nulls() throws Exception {
		final String expected = null;
		final String actual = null;
		final AttributeDifference diff = new AttributeDifference( "foo", expected, actual );

		assertThat( cut.matches( element, diff ) ).isFalse();
	}

	@Test
	void should_skip_non_pixel_strings() throws Exception {
		final String expected = "bar";
		final String actual = "baz";
		final AttributeDifference diff = new AttributeDifference( "foo", expected, actual );

		assertThat( cut.matches( element, diff ) ).isFalse();
	}

	@Test
	void input_should_control_toString() throws Exception {
		assertThat( new PixelDiffFilter( "0px", 0.0 ) ).hasToString( "pixel-diff=0px" );
		assertThat( new PixelDiffFilter( "0.0px", 0.0 ) ).hasToString( "pixel-diff=0.0px" );
	}

	@Test
	void legacy_should_be_migrated() throws Exception {
		assertThat( new PixelDiffFilter( "0", 0.0 ) ).hasToString( "pixel-diff=0px" );
		assertThat( new PixelDiffFilter( "0.0", 0.0 ) ).hasToString( "pixel-diff=0.0px" );
	}

	@Test
	void style_attribute_should_return_false() {
		final String expected = "width: 1px; height: 1px";
		final String actual = "width: 1000px; height: 1000px";
		final AttributeDifference diff = new AttributeDifference( "style", expected, actual );

		assertThat( cut.matches( element, diff ) ).isFalse();
	}

	@Test
	void box_shadow_should_not_be_parsed() throws Exception {
		final String expected =
				"rgba(0, 0, 0, 0.03) 0px 0px 1.18247px 0px, rgba(0, 0, 0, 0.06) 0px 1.18247px 2.36495px 0px";
		final String actual =
				"rgba(0, 0, 0, 0.098) 0px 0px 3.90236px 0px, rgba(0, 0, 0, 0.196) 0px 3.90236px 7.80472px 0px";
		final AttributeDifference difference = new AttributeDifference( "box-shadow", expected, actual );

		assertThat( cut.matches( element, difference ) ).isFalse();
	}

	@Test
	void multiple_px_values_for_colums_should_be_parsed() throws Exception {
		final String expected = "123px";
		final String actual = "124px";
		final AttributeDifference difference = new AttributeDifference( "grid-template-columns", expected, actual );

		assertThat( cut.matches( element, difference ) ).isTrue();
	}

	@Test
	void varying_multiple_px_values_for_colums_should_not_be_parsed() throws Exception {
		final String expected = "123px";
		final String actual = "64px 62px";
		final AttributeDifference difference = new AttributeDifference( "grid-template-columns", expected, actual );

		assertThat( cut.matches( element, difference ) ).isFalse();
	}

}
