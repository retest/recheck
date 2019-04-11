package de.retest.recheck.image;

import static de.retest.recheck.image.ImageCompare.assertThatImage;
import static de.retest.recheck.ui.image.ImageUtils.image2Screenshot;
import static de.retest.recheck.ui.image.ImageUtils.readImage;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.retest.recheck.image.ExactImageDifferenceCalculator;
import de.retest.recheck.image.ImageDifference;
import de.retest.recheck.ui.image.Screenshot;

public class ExactImageDifferenceCalculatorTest {

	final ExactImageDifferenceCalculator imgDiffCalc = new ExactImageDifferenceCalculator();

	//http://stackoverflow.com/a/25151302/58997
	@Test
	public void reallife_example_should_have_differences() throws IOException {
		final ImageDifference imgDiff = imgDiffCalc.compare( "src/test/resources/de/retest/image/PageRenderer_1.png",
				"src/test/resources/de/retest/image/PageRenderer_2.png" );

		assertThat( imgDiff.isEqual() ).isFalse();
		assertThat( imgDiff.getMatch() ).isLessThan( 1.0 );
		assertThat( imgDiff.getMatch() ).isGreaterThan( 0.5 );
		assertThatImage( imgDiff.getDifferenceImage() )
				.isEqualTo( new File( "src/test/resources/de/retest/image/PageRenderer_diff.png" ) );
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
