package de.retest.recheck.persistence.xml.util;

import static de.retest.recheck.util.FileUtil.readFromZipFile;
import static de.retest.recheck.util.FileUtil.writeToFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.image.Screenshot.ImageType;
import de.retest.recheck.util.FileUtil.Writer;
import de.retest.recheck.util.FileUtil.ZipReader;

class LazyScreenshotZipPersistenceTest {

	@TempDir
	Path tempDir;

	LazyScreenshotZipPersistence screenshotPersistence;

	byte[] imageBytes1;
	Screenshot screenshot1;
	String path1;

	byte[] imageBytes2;
	Screenshot screenshot2;
	String path2;

	Screenshot screenshot1duplicate;

	@BeforeEach
	void setUp() throws Exception {
		screenshotPersistence = new LazyScreenshotZipPersistence();

		imageBytes1 = "testcontent1".getBytes();
		screenshot1 = new Screenshot( "testimage1", imageBytes1, ImageType.PNG );
		path1 = "screenshot/" + screenshot1.getPersistenceId() + "." + screenshot1.getType().getFileExtension();

		imageBytes2 = "test2".getBytes();
		screenshot2 = new Screenshot( "testimage2", imageBytes2, ImageType.PNG );
		path2 = "screenshot/" + screenshot2.getPersistenceId() + "." + screenshot2.getType().getFileExtension();

		screenshot1duplicate = new Screenshot( "testimage1", imageBytes1, ImageType.PNG );
	}

	@Test
	void nothing_happens_if_there_is_no_screenshot() throws Exception {
		final Persistable obj1 = mock( Persistable.class );
		final Object obj2 = mock( Object.class );

		screenshotPersistence.getMarshallListener().afterMarshal( obj1 );
		screenshotPersistence.getMarshallListener().afterMarshal( obj2 );

		screenshotPersistence.getUnmarshallListener().afterUnmarshal( obj1, obj2 );
		screenshotPersistence.getUnmarshallListener().afterUnmarshal( obj2, obj1 );

		verifyNoInteractions( obj1 );
		verifyNoInteractions( obj2 );

		assertThat( screenshotPersistence.screenshots ).isEmpty();
	}

	@Test
	void marshallListener_fills_only_list() {
		final Screenshot obj1 = mock( Screenshot.class );
		final Persistable obj2 = mock( Persistable.class );
		final Screenshot obj3 = mock( Screenshot.class );

		screenshotPersistence.getMarshallListener().afterMarshal( obj1 );
		screenshotPersistence.getMarshallListener().afterMarshal( obj2 );
		screenshotPersistence.getMarshallListener().afterMarshal( obj3 );

		assertThat( screenshotPersistence.screenshots ).hasSize( 2 );
		assertThat( screenshotPersistence.screenshots ).contains( obj1 );
		assertThat( screenshotPersistence.screenshots ).contains( obj3 );

		verifyNoInteractions( obj1 );
		verifyNoInteractions( obj2 );
		verifyNoInteractions( obj3 );
	}

	@Test
	void unmarshallListener_fills_only_list() throws Exception {
		final Screenshot obj1 = mock( Screenshot.class );
		final Persistable obj2 = mock( Persistable.class );
		final Screenshot obj3 = mock( Screenshot.class );

		screenshotPersistence.getUnmarshallListener().afterUnmarshal( obj1, obj2 );
		screenshotPersistence.getUnmarshallListener().afterUnmarshal( obj2, obj3 );
		screenshotPersistence.getUnmarshallListener().afterUnmarshal( obj3, obj1 );

		assertThat( screenshotPersistence.screenshots ).hasSize( 2 );
		assertThat( screenshotPersistence.screenshots ).contains( obj1 );
		assertThat( screenshotPersistence.screenshots ).contains( obj3 );

		verifyNoInteractions( obj1 );
		verifyNoInteractions( obj2 );
		verifyNoInteractions( obj3 );
	}

	@Test
	void write_screenshots_to_zipfile_works() throws Exception {
		final File zipFile = getTmpZipfile();

		screenshotPersistence.screenshots.add( screenshot1 );
		screenshotPersistence.screenshots.add( screenshot2 );

		// write with productive code
		writeToFile( zipFile, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				final ZipOutputStream zout = new ZipOutputStream( out );
				screenshotPersistence.saveScreenshotsNow( zout );
				zout.finish();
			}
		} );

		// read with test code
		readFromZipFile( zipFile, new ZipReader<Object>() {
			@Override
			public Object read( final ZipFile in ) throws IOException {
				ZipEntry entry;

				entry = in.getEntry( path1 );
				assertThat( entry ).isNotNull();
				assertThat( IOUtils.toByteArray( in.getInputStream( entry ) ) ).isEqualTo( imageBytes1 );

				entry = in.getEntry( path2 );
				assertThat( entry ).isNotNull();
				assertThat( IOUtils.toByteArray( in.getInputStream( entry ) ) ).isEqualTo( imageBytes2 );

				return null;
			}
		} );
	}

	@Test
	void writing_of_duplicates() throws Exception {
		final File zipFile = getTmpZipfile();

		screenshotPersistence.screenshots.add( screenshot1 );
		screenshotPersistence.screenshots.add( screenshot1duplicate );

		// write with productive code
		writeToFile( zipFile, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				final ZipOutputStream zout = new ZipOutputStream( out );
				screenshotPersistence.saveScreenshotsNow( zout );
				zout.finish();
			}
		} );

		// read with test code
		readFromZipFile( zipFile, new ZipReader<Object>() {
			@Override
			public Object read( final ZipFile in ) throws IOException {
				final ArrayList<? extends ZipEntry> entries = Collections.list( in.entries() );

				assertThat( entries ).hasSize( 1 );
				final ZipEntry entry = entries.get( 0 );
				assertThat( entry.getName() ).isEqualTo( path1 );
				assertThat( IOUtils.toByteArray( in.getInputStream( entry ) ) ).isEqualTo( imageBytes1 );

				return null;
			}
		} );
	}

	@Test
	void read_screenshots_from_zipfile_works() throws Exception {
		final File zipFile = getTmpZipfile();
		// clear BinaryData for test
		screenshot1.setBinaryData( new byte[0] );
		screenshot2.setBinaryData( new byte[0] );

		screenshotPersistence.screenshots.add( screenshot1 );
		screenshotPersistence.screenshots.add( screenshot2 );

		// write with test code
		writeToFile( zipFile, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				final ZipOutputStream zout = new ZipOutputStream( out );

				zout.putNextEntry( new ZipEntry( path1 ) );
				zout.write( imageBytes1 );

				zout.putNextEntry( new ZipEntry( path2 ) );
				zout.write( imageBytes2 );

				zout.finish();
			}
		} );

		// read with productive code
		readFromZipFile( zipFile, new ZipReader<Object>() {
			@Override
			public Object read( final ZipFile in ) throws IOException {
				screenshotPersistence.loadScreenshotsNow( in );
				return null;
			}
		} );

		assertThat( screenshot1.getBinaryData() ).isNotEmpty();
		assertThat( screenshot1.getBinaryData() ).isEqualTo( imageBytes1 );
		assertThat( screenshot2.getBinaryData() ).isNotEmpty();
		assertThat( screenshot2.getBinaryData() ).isEqualTo( imageBytes2 );
	}

	@Test
	void reading_of_duplicates() throws Exception {
		final File zipFile = getTmpZipfile();

		screenshotPersistence.screenshots.add( screenshot1 );
		screenshotPersistence.screenshots.add( screenshot1duplicate );

		// write with test code
		writeToFile( zipFile, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				final ZipOutputStream zout = new ZipOutputStream( out );

				zout.putNextEntry( new ZipEntry( path1 ) );
				zout.write( imageBytes1 );

				zout.finish();
			}
		} );

		// read with productive code
		readFromZipFile( zipFile, new ZipReader<Object>() {
			@Override
			public Object read( final ZipFile in ) throws IOException {
				screenshotPersistence.loadScreenshotsNow( in );
				return null;
			}
		} );

		assertThat( screenshot1.getBinaryData() ).isNotEmpty();
		assertThat( screenshot1.getBinaryData() ).isEqualTo( imageBytes1 );
		assertThat( screenshot1duplicate.getBinaryData() ).isNotEmpty();
		assertThat( screenshot1duplicate.getBinaryData() ).isEqualTo( imageBytes1 );
	}

	@Test
	void reading_of_missing_screenshot_zip_entries() throws Exception {
		final File zipFile = getTmpZipfile();

		screenshotPersistence.screenshots.add( screenshot1 );
		final Pair<Screenshot, Screenshot> parent = Pair.of( screenshot1, screenshot2 );
		screenshotPersistence.screenshotParentMap.put( screenshot1, parent );

		// write with test code
		writeToFile( zipFile, new Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				final ZipOutputStream zout = new ZipOutputStream( out );
				zout.putNextEntry( new ZipEntry( path2 ) );
				zout.write( imageBytes2 );
				zout.finish();
			}
		} );

		// read with productive code
		readFromZipFile( zipFile, new ZipReader<Object>() {
			@Override
			public Object read( final ZipFile in ) throws IOException {
				screenshotPersistence.loadScreenshotsNow( in );
				return null;
			}
		} );

		assertThat( parent.getLeft() ).isNull();
	}

	@Test
	void screenshot_zippath_should_be_sanitized() {
		final Screenshot screenshot = new Screenshot( "../../testimage", new byte[] { 0 }, ImageType.PNG );
		assertThat( LazyScreenshotZipPersistence.createFilePath( screenshot ) ).isEqualTo( "screenshot/testimage.png" );
	}

	File getTmpZipfile() throws IOException {
		return tempDir.resolve( "tmptestzip" ).toFile();
	}

}
