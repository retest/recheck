package de.retest.image;

import static de.retest.image.ImageCompare.assertThatImage;
import static de.retest.ui.image.ImageUtils.image2Screenshot;
import static de.retest.ui.image.ImageUtils.readImage;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.retest.ui.image.Screenshot;

public class FuzzyImageDifferenceCalculatorTest {

	final FuzzyImageDifferenceCalculator imgDiffCalc = new FuzzyImageDifferenceCalculator();

	@Test
	public void compare_natural_images_should_find_differences() throws Exception {
		final ImageDifference imgDiff = imgDiffCalc.compare( "src/test/resources/de/retest/image/natural1.png",
				"src/test/resources/de/retest/image/natural2.png" );

		assertThat( imgDiff.getMatch() ).isLessThan( 1.0 );
		assertThat( imgDiff.getMatch() ).isGreaterThan( 0.5 );
		assertThatImage( imgDiff.getDifferenceImage() )
				.isEqualTo( new File( "src/test/resources/de/retest/image/natural_diff.png" ) );
	}

	@Test
	public void compare_natural_image_of_different_sizes_should_find_differences() throws Exception {
		final ImageDifference imgDiff = imgDiffCalc.compare( "src/test/resources/de/retest/image/natural1_small.png",
				"src/test/resources/de/retest/image/natural2.png" );

		assertThat( imgDiff.getMatch() ).isLessThan( 1.0 );
		assertThat( imgDiff.getMatch() ).isGreaterThan( 0.5 );
		assertThatImage( imgDiff.getDifferenceImage() )
				.isEqualTo( new File( "src/test/resources/de/retest/image/natural_small_diff.png" ) );
	}

	@Test
	public void compare_painted_images_should_find_differences() throws Exception {
		final ImageDifference imgDiff = imgDiffCalc.compare( "src/test/resources/de/retest/image/painted1.png",
				"src/test/resources/de/retest/image/painted2.png" );

		assertThat( imgDiff.getMatch() ).isLessThan( 1.0 );
		assertThat( imgDiff.getMatch() ).isGreaterThan( 0.5 );
		assertThatImage( imgDiff.getDifferenceImage() )
				.isEqualTo( new File( "src/test/resources/de/retest/image/painted_diff.png" ) );
	}

	@Test
	public void compare_same_image_should_have_no_differences() throws IOException {
		final ImageDifference imgDiff = imgDiffCalc.compare( "src/test/resources/de/retest/image/natural1.png",
				"src/test/resources/de/retest/image/natural1.png" );

		assertThat( imgDiff.getMatch() ).isEqualTo( 1.0 );
		assertThatImage( imgDiff.getDifferenceImage() )
				.isEqualTo( new File( "src/test/resources/de/retest/image/natural1.png" ) );
	}

	@Test
	public void compare_same_image_of_different_size_should_have_no_differences() throws IOException {
		final ImageDifference imgDiff = imgDiffCalc.compare( "src/test/resources/de/retest/image/natural1.png",
				"src/test/resources/de/retest/image/natural1_small.png" );

		assertThat( imgDiff.getMatch() ).isEqualTo( 1.0 );
		assertThatImage( imgDiff.getDifferenceImage() )
				.isEqualTo( new File( "src/test/resources/de/retest/image/natural1_small.png" ) );
	}

	@Test
	public void two_nulls_should_be_equal() throws Exception {
		assertThat( imgDiffCalc.compare( (Screenshot) null, (Screenshot) null ).isEqual() ).isTrue();
		assertThat( imgDiffCalc.compare( (BufferedImage) null, (BufferedImage) null ).isEqual() ).isTrue();
	}

	@Test
	public void null_values_should_cause_diff() throws Exception {
		final BufferedImage img = readImage( new File( "src/test/resources/de/retest/image/PageRenderer_1.png" ) );
		assertThat( imgDiffCalc.compare( image2Screenshot( "renderer", img ), (Screenshot) null ).isEqual() ).isFalse();
		assertThat( imgDiffCalc.compare( img, (BufferedImage) null ).isEqual() ).isFalse();
		assertThat( imgDiffCalc.compare( (Screenshot) null, image2Screenshot( "renderer", img ) ).isEqual() ).isFalse();
		assertThat( imgDiffCalc.compare( (BufferedImage) null, img ).isEqual() ).isFalse();
	}
}
