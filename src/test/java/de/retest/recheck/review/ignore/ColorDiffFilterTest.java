package de.retest.recheck.review.ignore;

import static de.retest.recheck.review.ignore.ColorDiffFilter.calculateColorDistance;
import static de.retest.recheck.review.ignore.ColorDiffFilter.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class ColorDiffFilterTest {

	double colorDiff = 5.0;
	Filter cut;
	Element element;

	@BeforeEach
	void setUp() {
		cut = new ColorDiffFilter( "5%", colorDiff );
		element = mock( Element.class );
	}

	static AttributeDifference toDiff( final String expected, final String actual ) {
		return new AttributeDifference( "color", expected, actual );
	}

	@Test
	void should_filter_diff_when_color_diff_is_not_exceeded() throws Exception {
		assertThat( cut.matches( element, toDiff( "rgb(255, 0, 0)", "rgb(250, 0, 0)" ) ) ).isTrue();
		assertThat( cut.matches( element, toDiff( "rgb(255, 255, 0)", "rgb(250, 250, 0)" ) ) ).isTrue();
	}

	@Test
	void should_not_filter_diff_when_color_diff_is_exceeded() throws Exception {
		assertThat( cut.matches( element, toDiff( "rgb(0, 0, 0)", "rgb(255, 255, 255)" ) ) ).isFalse();
		assertThat( cut.matches( element, toDiff( "rgb(255, 255, 255)", "rgb(250, 250, 250)" ) ) ).isFalse();
	}

	@Test
	void should_filter_when_diff_is_just_opacity_given() throws Exception {
		assertThat( cut.matches( element, toDiff( "rgb(255, 255, 255)", "rgb(255, 255, 255, 255)" ) ) ).isTrue();
	}

	@Test
	void should_skip_nulls() throws Exception {
		assertThat( cut.matches( element, new AttributeDifference( "color", "rgb(0,0,0)", null ) ) ).isFalse();
		assertThat( cut.matches( element, new AttributeDifference( "color", null, "rgb(0,0,0)" ) ) ).isFalse();
	}

	@Test
	void should_skip_non_color_strings() throws Exception {
		final String expected = "bar";
		final String actual = "baz";
		final AttributeDifference diff = new AttributeDifference( "foo", expected, actual );

		assertThat( cut.matches( element, diff ) ).isFalse();
	}

	@Test
	void complete_opacity_change_should_be_100_percent_change() throws Exception {
		assertThat( calculateColorDistance( Color.BLACK, new Color( 0, 0, 0, 0 ) ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( new Color( 0, 0, 0, 0 ), Color.BLACK ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( Color.RED, new Color( 0, 0, 0, 0 ) ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( new Color( 0, 0, 0, 0 ), Color.RED ) ).isEqualTo( 1.0 );
	}

	@Test
	void black_to_white_should_be_100_percent_change() throws Exception {
		assertThat( calculateColorDistance( Color.BLACK, Color.WHITE ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( Color.WHITE, Color.BLACK ) ).isEqualTo( 1.0 );
	}

	@Test
	void red_to_yellow_should_be_100_percent_change() throws Exception {
		assertThat( calculateColorDistance( Color.RED, Color.YELLOW ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( Color.YELLOW, Color.RED ) ).isEqualTo( 1.0 );
	}

	@Test
	void green_to_yellow_should_be_100_percent_change() throws Exception {
		assertThat( calculateColorDistance( Color.GREEN, Color.YELLOW ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( Color.YELLOW, Color.GREEN ) ).isEqualTo( 1.0 );
	}

	@Test
	void white_to_red_should_be_100_percent_change() throws Exception {
		assertThat( calculateColorDistance( Color.WHITE, Color.RED ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( Color.RED, Color.WHITE ) ).isEqualTo( 1.0 );
	}

	@Test
	void black_to_red_should_be_100_percent_change() throws Exception {
		assertThat( calculateColorDistance( Color.BLACK, Color.RED ) ).isEqualTo( 1.0 );
		assertThat( calculateColorDistance( Color.RED, Color.BLACK ) ).isEqualTo( 1.0 );
	}

	@Test
	void should_parse_both_rgb_and_rgba_correctly() {
		assertThat( parse( "rgb(0,0,0)" ) ).isEqualTo( Color.BLACK );
		assertThat( parse( "rgb(0,0,0,255)" ) ).isEqualTo( Color.BLACK );
		assertThat( parse( "rgb(0,0,0,255)" ) ).isEqualTo( new Color( 0, 0, 0, 255 ) );
		assertThat( parse( "rgb(255,255,255,255)" ) ).isEqualTo( new Color( 255, 255, 255, 255 ) );
	}

	@Test
	void should_handle_whitespace() {
		assertThat( parse( "rgb(0, 0, 0)" ) ).isEqualTo( Color.BLACK );
		assertThat( parse( "rgb(0, 0, 0, 255)" ) ).isEqualTo( Color.BLACK );
	}

	@Test
	void invalid_values_should_return_null() {
		assertThat( parse( "rgb(0,2fw,0)" ) ).isNull();
		assertThat( parse( "rgb(-1,0,0)" ) ).isNull();
		assertThat( parse( "rgb(0,0,322)" ) ).isNull();
		assertThat( parse( "rgb(0,0,0,255.021)" ) ).isNull();
	}
}
