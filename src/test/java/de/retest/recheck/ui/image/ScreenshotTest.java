package de.retest.recheck.ui.image;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.image.Screenshot.ImageType;

class ScreenshotTest {

	@Test
	void same_prefix_different_content_should_not_be_equal() {
		final Screenshot shot1 = new Screenshot( "some", new byte[] { 1, 1, 1, 6, 3, 5, 2, 1, 2 }, ImageType.PNG );
		final Screenshot shot2 = new Screenshot( "some", new byte[] { 1, 2, 4, 6, 3, 4, 7, 8, 2 }, ImageType.PNG );

		assertThat( shot1.equals( shot2 ) ).isFalse();
	}

	@Test
	void updating_content_should_update_sha() {
		final byte[] same = new byte[] { 1, 1, 1, 6, 3, 5, 2, 1, 2 };
		final Screenshot shot1 = new Screenshot( "some", same, ImageType.PNG );
		final Screenshot shot2 = new Screenshot( "some", new byte[] { 1, 2, 4, 6, 3, 4, 7, 8, 2 }, ImageType.PNG );

		assertThat( shot1.equals( shot2 ) ).isFalse();

		shot2.setBinaryData( same );
		assertThat( shot1.equals( shot2 ) ).isTrue();

		shot1.setBinaryData( new byte[] { 1, 4, 5, 6, 2, 3, 4, 5 } );
		assertThat( shot1.equals( shot2 ) ).isFalse();
	}

	@Test
	void updating_should_be_null_save() {
		final Screenshot shot = new Screenshot( "some", new byte[] { 1, 2, 4, 6, 3, 4, 7, 8, 2 }, ImageType.PNG );
		shot.setBinaryData( null );
	}
}
