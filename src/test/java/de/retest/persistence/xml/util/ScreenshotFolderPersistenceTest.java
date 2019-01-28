package de.retest.persistence.xml.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.retest.persistence.Persistable;
import de.retest.ui.image.Screenshot;
import de.retest.ui.image.Screenshot.ImageType;
import de.retest.util.FileUtil;
import de.retest.util.FileUtil.Writer;

public class ScreenshotFolderPersistenceTest {

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	ScreenshotFolderPersistence screenshotPersistence;
	File baseFolder;
	File screenshotFolder;

	byte[] filledImageBytes;
	Screenshot filledScreenshot;
	File filledImageFile;

	Screenshot emptyScreenshot;
	File emptyImageFile;

	@Before
	public void setUp() throws Exception {
		baseFolder = temp.newFolder();
		screenshotFolder = new File( baseFolder, "screenshot" );
		screenshotPersistence = new ScreenshotFolderPersistence( baseFolder );

		filledImageBytes = "testcontent1".getBytes();
		filledScreenshot = new Screenshot( "testimage", filledImageBytes, ImageType.PNG );
		filledImageFile = new File( screenshotFolder,
				filledScreenshot.getPersistenceId() + "." + ImageType.PNG.getFileExtension() );

		emptyScreenshot = new Screenshot( "emptyimage", new byte[0], ImageType.PNG );
		emptyImageFile = new File( screenshotFolder,
				emptyScreenshot.getPersistenceId() + "." + ImageType.PNG.getFileExtension() );
	}

	@Test
	public void nothing_happens_if_there_is_no_screenshot() throws Exception {
		final Persistable obj1 = mock( Persistable.class );
		final Object obj2 = mock( Object.class );

		screenshotPersistence.getMarshallListener().afterMarshal( obj1 );
		screenshotPersistence.getMarshallListener().afterMarshal( obj2 );
		screenshotPersistence.getUnmarshallListener().afterUnmarshal( obj1, obj2 );
		screenshotPersistence.getUnmarshallListener().afterUnmarshal( obj2, obj1 );

		verifyZeroInteractions( obj1 );
		verifyZeroInteractions( obj2 );
	}

	@Test
	public void save_screenshot_to_image_file() {
		screenshotPersistence.getMarshallListener().afterMarshal( filledScreenshot );

		assertThat( filledImageFile ).exists();
		assertThat( filledImageFile ).hasBinaryContent( filledImageBytes );
	}

	@Test
	public void save_works_with_existing_dir() throws Exception {
		screenshotPersistence.getMarshallListener().afterMarshal( filledScreenshot );
		assertThat( filledImageFile ).exists();
	}

	// TODO what if screenshot folder is a file?
	// TODO what if screenshot folder is not write able?

	@Test
	public void make_screenshot_dir_lazy() throws Exception {
		assertThat( screenshotFolder ).doesNotExist();

		screenshotPersistence.getMarshallListener().afterMarshal( filledScreenshot );
		assertThat( screenshotFolder ).exists();
	}

	@Test
	public void make_no_screenshot_dir_if_there_are_no_images() throws Exception {
		screenshotPersistence.getMarshallListener().afterMarshal( new Object() );
		screenshotPersistence.getMarshallListener().afterMarshal( "" );

		assertThat( screenshotFolder ).doesNotExist();
	}

	// load

	@Test
	public void load_image_to_screenshot() throws Exception {
		createImageFileForEmptyScreenshot();

		screenshotPersistence.getUnmarshallListener().afterUnmarshal( emptyScreenshot, null );

		assertThat( emptyScreenshot.getBinaryData() ).isEqualTo( filledImageBytes );
	}

	@Test
	public void load_not_existing_image_to_screenshot() {
		final Pair<Screenshot, Screenshot> parent = Pair.of( emptyScreenshot, emptyScreenshot );
		screenshotPersistence.getUnmarshallListener().afterUnmarshal( emptyScreenshot, parent );
		screenshotPersistence.getUnmarshallListener().afterUnmarshal( parent, new Object() );

		assertThat( parent.getLeft() ).isNull();
		assertThat( parent.getRight() ).isNull();
	}

	private void createImageFileForEmptyScreenshot() throws IOException {
		FileUtil.writeToFile( emptyImageFile, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				out.write( filledImageBytes );
			}
		} );
	}

}
