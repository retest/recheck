package de.retest.recheck.util;

import static de.retest.recheck.util.FileUtil.convertListOfFiles2SemicolonJoinedString;
import static de.retest.recheck.util.FileUtil.convertSemicolonSeparatedString2ListOfFiles;
import static de.retest.recheck.util.FileUtil.getFileSizeInMB;
import static de.retest.recheck.util.FileUtil.removeExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import de.retest.recheck.ioerror.ReTestSaveException;

public class FileUtilTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder( SystemUtils.IS_OS_MAC //
			? new File( "/private/" + System.getProperty( "java.io.tmpdir" ) )
			: new File( System.getProperty( "java.io.tmpdir" ) ) );

	private File getExecDir() {
		return new File( System.getProperty( "user.dir" ) );
	}

	@Test
	public void testConvertFileList2ListOfFiles_empty() throws IOException {
		assertThat( convertSemicolonSeparatedString2ListOfFiles( getExecDir(), "" ) ).hasSize( 0 );
		assertThat( convertSemicolonSeparatedString2ListOfFiles( getExecDir(), null ) ).hasSize( 0 );
	}

	@Test
	public void testConvertFileList2ListOfFiles_single_file() throws IOException {
		final List<File> result =
				convertSemicolonSeparatedString2ListOfFiles( getExecDir(), "src/test/resources/ImageUtilsTest.png" );
		assertThat( result.size() ).isEqualTo( 1 );
		assertThat( result.get( 0 ).getName() ).isEqualTo( "ImageUtilsTest.png" );
	}

	@Test
	public void testConvertFileList2ListOfFiles_multiple_files() throws IOException {
		final List<File> result = convertSemicolonSeparatedString2ListOfFiles( getExecDir(),
				"src/test/resources/ImageUtilsTest.png;src/test/resources/de/retest/image/img1.png" );
		assertThat( result.size() ).isEqualTo( 2 );
		assertThat( result.get( 0 ).getName() ).isEqualTo( "ImageUtilsTest.png" );
		assertThat( result.get( 1 ).getName() ).isEqualTo( "img1.png" );
	}

	@Test
	public void testConvertListOfFiles2FileList_empty_Files() throws IOException {
		assertThat( convertListOfFiles2SemicolonJoinedString( new File( "target" ), new ArrayList<File>() ) )
				.isEqualTo( "" );
		assertThat( convertListOfFiles2SemicolonJoinedString( new File( "src/test/resources" ), null ) )
				.isEqualTo( "" );
	}

	@Test
	public void testConvertListOfFiles2FileList_single_File() throws IOException {
		final List<File> files = new ArrayList<>();
		files.add( new File( "src/test/resources/version.txt" ) );
		assertThat( convertListOfFiles2SemicolonJoinedString( new File( "src" ), files ) )
				.isEqualTo( "test" + File.separator + "resources" + File.separator + "version.txt" );
		assertThat( convertListOfFiles2SemicolonJoinedString( new File( "src/test/resources" ), files ) )
				.isEqualTo( "version.txt" );
		//		assertEquals( "version.txt",
		//		        convertListOfFiles2SemicolonJoinedString( new File( "src/test/resources"), files ) );
	}

	@Test
	public void testConvertListOfFiles2FileList_multiple_Files() throws IOException {
		final List<File> files = new ArrayList<>();
		files.add( new File( "src/test/resources/version.txt" ) );
		files.add( new File( "src/test/resources/de/retest/util/login.png" ) );
		assertThat( convertListOfFiles2SemicolonJoinedString( new File( "src" ), files ) )
				.isEqualTo( "test/resources/version.txt;test/resources/de/retest/util/login.png" );
		assertThat( convertListOfFiles2SemicolonJoinedString( new File( "src/test/resources" ), files ) )
				.isEqualTo( "version.txt;de/retest/util/login.png" );
	}

	@Test
	public void test_removeExtension() throws IOException {
		assertThat( removeExtension( "" ) ).isEqualTo( "" );
		assertThat( removeExtension( ".test" ) ).isEqualTo( "" );
		assertThat( removeExtension( "me" ) ).isEqualTo( "me" );
		assertThat( removeExtension( "me.test" ) ).isEqualTo( "me" );
		assertThat( removeExtension( "me.too.test" ) ).isEqualTo( "me.too" );
		assertThat( removeExtension( "me." ) ).isEqualTo( "me" );
	}

	@Test
	public void test_getFileSizeInMB() throws Exception {
		final URL resourceUrl = FileUtilTest.class.getResource( "/ImageUtilsTestResize.png" );
		final String path = resourceUrl.toURI().getSchemeSpecificPart();
		final File login = new File( path );
		assertThat( getFileSizeInMB( login ) ).isEqualTo( 0.39267635345458984d, offset( 0.00000001d ) );
	}

	@Test
	public void writeFile_should_write_File() throws Exception {
		final File contendFolder = temp.newFolder();
		final File fileToBeWritten = new File( contendFolder, "fileToBeWritten" );

		FileUtil.writeToFile( fileToBeWritten, new FileUtil.Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				out.write( 0x41 );
			}
		} );

		assertThat( fileToBeWritten ).exists();
		assertThat( FileUtil.readFileToString( fileToBeWritten ) ).isEqualTo( "A" );
	}

	@Test
	public void writeFile_should_write_File_even_is_parentFolder_do_not_exsist() throws Exception {
		final File contendFolder = temp.newFolder();
		final File fileToBeWritten = new File( contendFolder, "nonExsistingFolder/fileToBeWritten.txt" );

		FileUtil.writeToFile( fileToBeWritten, new FileUtil.Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				out.write( 0x41 );
			}
		} );

		assertThat( fileToBeWritten ).exists();
		assertThat( FileUtil.readFileToString( fileToBeWritten ) ).isEqualTo( "A" );
	}

	@Test
	public void writeFile_should_write_File_even_File_already_exsists() throws Exception {
		final File contendFolder = temp.newFolder();
		final File fileToBeWritten = new File( contendFolder, "fileToBeWritten" );
		FileUtil.writeToFile( fileToBeWritten, new FileUtil.Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				out.write( 0x43 );
			}
		} );

		FileUtil.writeToFile( fileToBeWritten, new FileUtil.Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				out.write( 0x41 );
			}
		} );

		assertThat( fileToBeWritten ).exists();
		assertThat( FileUtil.readFileToString( fileToBeWritten ) ).isEqualTo( "A" );
	}

	@Test( expected = ReTestSaveException.class )
	public void writeFile_should_throw_exception_if_error_occurs() throws Exception {
		FileUtil.writeToFile( new File( "." ), new FileUtil.Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				throw new IOException( "Something bad happened" );
			}
		} );
	}

	@Test( expected = ReTestSaveException.class )
	public void writeFile_should_throw_exception_if_writer_is_null() throws Exception {
		final File contendFolder = temp.newFolder();
		final File fileToBeWritten = new File( contendFolder, "nonExsistingFolder/fileToBeWritten.txt" );

		FileUtil.writeToFile( fileToBeWritten, null );
	}

	@Test( expected = ReTestSaveException.class )
	public void writeFile_should_throw_exception_if_file_is_a_directory() throws Exception {
		final File contendFolder = temp.newFolder();

		FileUtil.writeToFile( contendFolder, new FileUtil.Writer() {
			@Override
			public void write( final FileOutputStream out ) throws IOException {
				out.write( 0x41 );
			}
		} );
	}

	@Test( expected = NullPointerException.class )
	public void listFilesRecursively_should_throw_exception_list_if_folder_is_null() throws Exception {
		final javax.swing.filechooser.FileNameExtensionFilter filter =
				new javax.swing.filechooser.FileNameExtensionFilter( "textfiles", "txt" );

		FileUtil.listFilesRecursively( null, filter );
	}

	@Test( expected = IllegalArgumentException.class )
	public void listFilesRecursively_should_throw_exception_if_folder_does_not_exsist() throws Exception {
		final javax.swing.filechooser.FileNameExtensionFilter filter =
				new javax.swing.filechooser.FileNameExtensionFilter( "textfiles", "txt" );

		FileUtil.listFilesRecursively( new File( "notExsistingFolder" ), filter );
	}

	@Test
	public void listFilesRecursively_should_get_only_files_with_filter() throws Exception {
		final File contendFolder = temp.newFolder( "testfolder" );
		final File testFolder1 = new File( contendFolder.getAbsolutePath() + File.separator + "testFolder1" );
		testFolder1.mkdirs();
		final File testFolder11 = new File( testFolder1.getAbsolutePath() + File.separator + "testFolder11" );
		testFolder11.mkdirs();
		final File testFolder2 = new File( contendFolder.getAbsolutePath() + File.separator + "testFolder2" );
		testFolder2.mkdirs();
		final File testFile0 = File.createTempFile( "testFile0", ".txt", contendFolder );
		final File testFile1 = File.createTempFile( "testFile1", ".txt", testFolder1 );
		final File testFile11 = File.createTempFile( "testFile11", ".txt", testFolder11 );
		final File testFile2 = File.createTempFile( "testFile2", ".txt", testFolder2 );
		File.createTempFile( "testFile0_", ".png", contendFolder );
		File.createTempFile( "testFile1_", ".exe", testFolder1 );
		File.createTempFile( "testFile11_", ".pdf", testFolder11 );
		File.createTempFile( "testFile2_", ".doc", testFolder2 );

		final javax.swing.filechooser.FileNameExtensionFilter filter =
				new javax.swing.filechooser.FileNameExtensionFilter( "textfiles", "txt" );

		final List<File> result = FileUtil.listFilesRecursively( contendFolder, filter );
		assertThat( result ).containsOnly( testFile0, testFile1, testFile11, testFile2, testFolder1, testFolder11,
				testFolder2 );
	}

	@Test
	public void listFiles_with_null_should_return_zero_files() throws Exception {
		final File file = Mockito.mock( File.class );
		Mockito.when( file.listFiles() ).thenReturn( null );
		Mockito.when( file.isDirectory() ).thenReturn( true );
		FileUtil.listFilesRecursively( file, new FileFilter() {
			@Override
			public boolean accept( final File arg0 ) {
				return false;
			}
		} );
		FileUtil.listFilesRecursively( file, new FilenameFilter() {

			@Override
			public boolean accept( final File file, final String s ) {
				return false;
			}
		} );
		FileUtil.listFilesRecursively( file, new javax.swing.filechooser.FileFilter() {
			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public boolean accept( final File file ) {
				return false;
			}
		} );
	}

	// readableCanonicalFileOrNull

	@Test
	public void readableCanonicalFileOrNull_return_file() throws Exception {
		final File file = temp.newFile();
		assertThat( FileUtil.readableCanonicalFileOrNull( file ) ).isEqualTo( file );
	}

	@Test
	public void readableCanonicalFileOrNull_dont_return_unreadable_file() throws Exception {
		final File file = temp.newFile();
		file.setReadable( false );
		assertThat( FileUtil.readableCanonicalFileOrNull( file ) ).isNull();
	}

	@Test
	public void readableCanonicalFileOrNull_dont_return_unexisting_file() throws Exception {
		final File file = temp.newFile();
		file.delete();
		assertThat( FileUtil.readableCanonicalFileOrNull( file ) ).isNull();
	}

	// readableWriteableCanonicalDirectoryOrNull

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_return_directory() throws Exception {
		final File file = temp.newFolder();
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( file ) ).isEqualTo( file );
	}

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_dont_returns_unreadable() throws Exception {
		final File file = temp.newFolder();
		file.setReadable( false );
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( file ) ).isNull();
	}

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_dont_returns_unwriteable() throws Exception {
		final File file = temp.newFolder();
		file.setWritable( false );
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( file ) ).isNull();
	}

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_dont_returns_unexecutable() throws Exception {
		final File file = temp.newFolder();
		file.setExecutable( false );
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( file ) ).isNull();
	}

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_return_directory_if_parent_is_read_write_and_executable()
			throws Exception {
		final File parent = temp.newFolder();
		final File directory = new File( parent, "child" );
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( directory ) ).isEqualTo( directory );
	}

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_dont_returns_unreadable_parent() throws Exception {
		final File parent = temp.newFolder();
		final File directory = new File( parent, "child" );
		parent.setReadable( false );
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( directory ) ).isNull();
	}

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_dont_returns_unwriteable_parent() throws Exception {
		final File parent = temp.newFolder();
		final File directory = new File( parent, "child" );
		parent.setWritable( false );
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( directory ) ).isNull();
	}

	@Test
	public void readableWriteableCanonicalDirectoryOrNull_dont_returns_unexecutable_parent() throws Exception {
		final File parent = temp.newFolder();
		final File directory = new File( parent, "child" );
		parent.setExecutable( false );
		assertThat( FileUtil.readableWriteableCanonicalDirectoryOrNull( directory ) ).isNull();
	}
}
