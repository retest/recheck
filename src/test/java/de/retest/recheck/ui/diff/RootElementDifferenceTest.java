package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.image.ImageDifference;
import de.retest.recheck.image.ImageDifferenceCalculator;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.ScreenshotAttributeDifference;
import de.retest.recheck.ui.image.Screenshot;

class RootElementDifferenceTest {

	RootElementDifference cut;

	@BeforeEach
	public void setup() {
		final Screenshot expected = mock( Screenshot.class );
		final Screenshot actual = mock( Screenshot.class );
		final AttributeDifference screenshotDiff = new ScreenshotAttributeDifference( expected, actual,
				TestImageDifferenceCalculator.class.getName(), 0.99 );
		final AttributesDifference attributesDiff =
				new AttributesDifference( Collections.singletonList( screenshotDiff ) );
		final Element eleme = mock( Element.class );
		when( eleme.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );
		final ElementDifference elementDifference = new ElementDifference( eleme, attributesDiff,
				mock( IdentifyingAttributesDifference.class ), expected, actual, new ArrayList<>() );
		cut = new RootElementDifference( elementDifference, mock( RootElement.class ), mock( RootElement.class ) );
	}

	@Test
	void getScreenshotDifference_should_find_difference() {
		assertThat( cut.getScreenshotDifference() ).isNotNull();
	}

	@Test
	void getExpectedScreenshot_should_recalc_diff() throws Exception {
		// image2Screenshot will return null instead of mocked screenshot.
		assertThat( cut.getExpectedScreenshot() ).isNull();
	}

	static class TestImageDifferenceCalculator implements ImageDifferenceCalculator {

		@Override
		public ImageDifference compare( final BufferedImage img1, final BufferedImage img2 ) {
			return mock( ImageDifference.class );
		}

		@Override
		public ImageDifference compare( final Screenshot expected, final Screenshot actual ) {
			return mock( ImageDifference.class );
		}

	}

}
