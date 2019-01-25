package de.retest.ui.image;

import static de.retest.ui.image.ImageUtils.MARKING_WIDTH;
import static de.retest.ui.image.ImageUtils.removeFileExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

public class ImageUtilsTest {

	static final String LOGIN_PNG = "src/test/resources/ImageUtilsTest.png";
	static final String RESIZE_PNG = "src/test/resources/ImageUtilsTestResize.png";
	static final int LOGIN_WIDTH = 256;
	static final int LOGIN_HEIGHT = 97;

	@Test
	public void scaling_result_should_be_proportional() throws IOException {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		assertThat( image.getWidth( null ) ).isEqualTo( LOGIN_WIDTH );
		assertThat( image.getHeight( null ) ).isEqualTo( LOGIN_HEIGHT );

		final Image scaled = ImageUtils.scaleProportionallyToMaxWidthHeight( image, 25, 25 );
		assertThat( scaled.getWidth( null ) ).isEqualTo( 25 );
		assertThat( scaled.getHeight( null ) ).isEqualTo( 9 );
	}

	@Test
	public void marking_at_0_should_increase_imagesize() throws IOException {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage marked = ImageUtils.mark( image, new Rectangle( 0, 5, 96, 16 ) );
		// ImageUtils.exportImage(marked, new File(FileUtils.createTempFolder(),
		// "login_marked_Benutzer.png"));
		assertThat( marked.getWidth() ).isEqualTo( LOGIN_WIDTH + MARKING_WIDTH );
	}

	@Test
	public void marking_until_imageborder_should_increase_imagesize() throws IOException {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage marked = ImageUtils.mark( image, new Rectangle( 0, 0, LOGIN_WIDTH, 57 ) );
		// ImageUtils.exportImage(marked, new File(FileUtils.createTempFolder(),
		// "login_marked_Panel.png"));
		assertThat( marked.getWidth() ).isEqualTo( LOGIN_WIDTH + 2 * MARKING_WIDTH );
		assertThat( marked.getHeight() ).isEqualTo( LOGIN_HEIGHT + MARKING_WIDTH );
	}

	@Test
	public void marking_bigger_than_image_should_increase_imagesize() throws IOException {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage marked = ImageUtils.mark( image, new Rectangle( 1, 0, 10, 10 ) );
		// ImageUtils.exportImage(marked, new File(FileUtils.createTempFolder(),
		// "login_marked_Panel.png"));
		assertThat( marked.getWidth() ).isEqualTo( 256 + MARKING_WIDTH - 1 );
	}

	@Test
	public void multiple_markings_should_not_increase_imagesize_further() throws IOException {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final List<Rectangle> markings = new ArrayList<>();
		markings.add( new Rectangle( 0, 0, LOGIN_WIDTH, 10 ) );
		markings.add( new Rectangle( 0, 10, LOGIN_WIDTH, 20 ) );
		markings.add( new Rectangle( 0, 20, LOGIN_WIDTH, 30 ) );
		final BufferedImage marked = ImageUtils.mark( image, markings );
		assertThat( marked.getWidth() ).isEqualTo( LOGIN_WIDTH + 2 * MARKING_WIDTH );
		assertThat( marked.getHeight() ).isEqualTo( LOGIN_HEIGHT + MARKING_WIDTH );
	}

	@Test
	public void cut_image_should_have_bounds_length() throws IOException {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage cut = ImageUtils.cutImage( image, new Rectangle( 10, 10, 20, 20 ) );
		assertThat( cut.getWidth() ).isEqualTo( 20 );
		assertThat( cut.getHeight() ).isEqualTo( 20 );
	}

	@Test
	public void high_cutToMax_image_should_have_max_height_unchanged_width() throws IOException {
		final BufferedImage image = new BufferedImage( 50, 10000, BufferedImage.TYPE_INT_ARGB );
		final BufferedImage cut = ImageUtils.cutToMax( image );
		assertThat( cut.getWidth() ).isEqualTo( 50 );
		assertThat( cut.getHeight() ).isEqualTo( ImageUtils.MAX_SCREENSHOT_HEIGHT_DEFAULT );
	}

	@Test
	public void smalles_possible_height_should_work() throws Exception {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage cut = ImageUtils.cutImage( image, new Rectangle( 0, image.getHeight() - 1, 20, 20 ) );
		assertThat( cut.getHeight() ).isEqualTo( 1 );
	}

	@Test
	public void exact_height_should_return_null() throws Exception {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage cut = ImageUtils.cutImage( image, new Rectangle( 0, image.getHeight(), 20, 20 ) );
		Assertions.assertThat( cut ).isNull();
	}

	@Test
	public void smalles_possible_width_should_work() throws Exception {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage cut = ImageUtils.cutImage( image, new Rectangle( image.getWidth() - 1, 10, 20, 20 ) );
		assertThat( cut.getWidth() ).isEqualTo( 1 );
	}

	@Test
	public void exact_width_should_return_null() throws Exception {
		final BufferedImage image = ImageUtils.readImage( LOGIN_PNG );
		final BufferedImage cut = ImageUtils.cutImage( image, new Rectangle( image.getWidth(), 10, 20, 20 ) );
		Assertions.assertThat( cut ).isNull();
	}

	@Test
	public void wide_cutToMax_image_should_have_max_width_unchanged_height() throws IOException {
		final BufferedImage image = new BufferedImage( 10000, 50, BufferedImage.TYPE_INT_ARGB );
		final BufferedImage cut = ImageUtils.cutToMax( image );
		assertThat( cut.getWidth() ).isEqualTo( ImageUtils.MAX_SCREENSHOT_WIDTH_DEFAULT );
		assertThat( cut.getHeight() ).isEqualTo( 50 );
	}

	@Test
	public void wide_and_high_cutToMax_image_should_have_max_width_and_height() throws IOException {
		final BufferedImage image = new BufferedImage( ImageUtils.MAX_SCREENSHOT_WIDTH_DEFAULT + 5,
				ImageUtils.MAX_SCREENSHOT_HEIGHT_DEFAULT + 5, BufferedImage.TYPE_INT_ARGB );
		final BufferedImage cut = ImageUtils.cutToMax( image );
		assertThat( cut.getWidth() ).isEqualTo( ImageUtils.MAX_SCREENSHOT_WIDTH_DEFAULT );
		assertThat( cut.getHeight() ).isEqualTo( ImageUtils.MAX_SCREENSHOT_HEIGHT_DEFAULT );
	}

	@Test
	public void removeFileExtension_removes_file_extension() throws Exception {
		assertThat( removeFileExtension( "path/to/file.png" ) ).isEqualTo( "path/to/file" );
		assertThat( removeFileExtension( "path/to/file.PNG" ) ).isEqualTo( "path/to/file" );
		assertThat( removeFileExtension( "file.PNG" ) ).isEqualTo( "file" );
		assertThat( removeFileExtension( "path\\to\\file.png" ) ).isEqualTo( "path\\to\\file" );
		assertThat( removeFileExtension( "path\\to\\file" ) ).isEqualTo( "path\\to\\file" );
		assertThat( removeFileExtension( "file" ) ).isEqualTo( "file" );
		assertThat( removeFileExtension( "file.PnG" ) ).isEqualTo( "file" );
		assertThat( removeFileExtension( "" ) ).isEqualTo( "" );
	}

	@Test
	public void icon_path_should_not_contain_paths() throws Exception {
		final Icon winFileIcon = Mockito.mock( Icon.class );
		Mockito.when( winFileIcon.toString() ).thenReturn( "C:/Some/path/ with blanks /icon.png" );
		Assertions.assertThat( ImageUtils.getTextFromIcon( winFileIcon ) ).isEqualTo( "icon.png" );

		final Icon linuxFileIcon = Mockito.mock( Icon.class );
		Mockito.when( linuxFileIcon.toString() ).thenReturn( "/Some/path/ with blanks /icon.png" );
		Assertions.assertThat( ImageUtils.getTextFromIcon( linuxFileIcon ) ).isEqualTo( "icon.png" );
	}

	@Test
	public void icon_path_should_not_contain_paths_file_protocol() throws Exception {
		final Icon winIcon = Mockito.mock( Icon.class );
		Mockito.when( winIcon.toString() ).thenReturn( "file:C:/Some/path/ with blanks /icon.png" );
		Assertions.assertThat( ImageUtils.getTextFromIcon( winIcon ) ).isEqualTo( "icon.png" );

		final Icon linuxIcon = Mockito.mock( Icon.class );
		Mockito.when( linuxIcon.toString() ).thenReturn( "file:/Some/path/ with blanks /icon.png" );
		Assertions.assertThat( ImageUtils.getTextFromIcon( linuxIcon ) ).isEqualTo( "icon.png" );
	}

	@Test
	public void icon_path_should_not_contain_paths_jar_protocol() throws Exception {
		final Icon jarWinIcon = Mockito.mock( Icon.class );
		Mockito.when( jarWinIcon.toString() )
				.thenReturn( "jar:file:C:/Some/path/ with blanks /icons.jar!/com/package/icon.png" );
		Assertions.assertThat( ImageUtils.getTextFromIcon( jarWinIcon ) )
				.isEqualTo( "icons.jar!/com/package/icon.png" );

		final Icon jarLinuxIcon = Mockito.mock( Icon.class );
		Mockito.when( jarLinuxIcon.toString() )
				.thenReturn( "jar:file:/Some/path/ with blanks /icons.jar!/com/package/icon.png" );
		Assertions.assertThat( ImageUtils.getTextFromIcon( jarLinuxIcon ) )
				.isEqualTo( "icons.jar!/com/package/icon.png" );
	}

	@Test
	public void icon_path_should_not_contain_paths_bundleresource() throws Exception {
		final Icon bundleresourceIcon = Mockito.mock( Icon.class );
		Mockito.when( bundleresourceIcon.toString() )
				.thenReturn( "file:bundleresource:/Some/path/ with blanks /icon.png" );
		Assertions.assertThat( ImageUtils.getTextFromIcon( bundleresourceIcon ) ).isEqualTo( "icon.png" );
	}

	@Test
	public void cutImage_should_return_null_if_BufferedImage_is_null() throws Exception {
		final BufferedImage image = null;
		final Rectangle rectangle = mock( Rectangle.class );
		assertThat( ImageUtils.cutImage( image, rectangle ) ).isNull();
	}

	@Test
	public void cutImage_should_return_null_if_Rectangle_is_null() throws Exception {
		final BufferedImage image = mock( BufferedImage.class );
		final Rectangle rectangle = null;
		assertThat( ImageUtils.cutImage( image, rectangle ) ).isNull();
	}

	@Test
	public void resizeImage_should_return_a_correctly_shaped_image() throws Exception {
		final BufferedImage image = ImageUtils.readImage( RESIZE_PNG );
		final int newWith = 900;
		final int newHeight = 2800;
		final BufferedImage resizedImage = ImageUtils.resizeImage( image, newWith, newHeight );
		assertThat( resizedImage.getWidth() ).isEqualTo( newWith );
		assertThat( resizedImage.getHeight() ).isEqualTo( newHeight );
	}
}
