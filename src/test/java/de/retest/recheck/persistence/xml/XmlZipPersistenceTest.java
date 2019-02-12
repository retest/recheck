package de.retest.recheck.persistence.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import de.retest.recheck.ioerror.ReTestLoadException;
import de.retest.recheck.ioerror.ReTestSaveException;
import de.retest.recheck.util.ApprovalsUtil;
import de.retest.recheck.util.FileUtil;
import de.retest.recheck.util.FileUtil.ZipReader;

@ExtendWith( TempDirectory.class )
public class XmlZipPersistenceTest {

	File baseFolder;

	XmlZipPersistence<TestPersistable> persistence;
	private XmlTransformer xmlTransformer;

	@BeforeEach
	public void setUp( @TempDir final Path temp ) {
		baseFolder = temp.toFile();
		xmlTransformer = new XmlTransformer( TestPersistable.class );
		persistence = new XmlZipPersistence<>( xmlTransformer );
	}

	@Test
	public void simple_save_to_file() throws Exception {
		final TestPersistable element = new TestPersistable();

		persistence.save( baseFolder.toURI(), element );

		assertThat( baseFolder ).exists();
		ApprovalsUtil.verifyXml( readReTestXml( baseFolder ) );
	}

	private String readReTestXml( final File file ) throws IOException {
		return FileUtil.readFromZipFile( file, new ZipReader<String>() {
			@Override
			public String read( final ZipFile in ) throws IOException {
				final ZipEntry entry = in.getEntry( "retest.xml" );
				return IOUtils.toString( in.getInputStream( entry ), StandardCharsets.UTF_8 );
			}
		} );
	}

	// TODO test case for existing file

	@Test
	public void try_to_save_to_file_where_a_folder_with_the_same_name_already_exists() throws Exception {
		final TestPersistable element = new TestPersistable();
		try {
			persistence.save( baseFolder.toURI(), element );
		} catch ( final Exception e ) {
			assertThat( e ).isInstanceOf( ReTestSaveException.class );
		}
	}

	@Test
	public void simple_load_from_folder() throws Exception {
		final URI identifier = FileUtil.getFileUriForClasspathRelativPath( "/persistence/simple_zip_persisted.zip" );

		final TestPersistable element = persistence.load( identifier );

		Assertions.assertThat( element ).isNotNull();
	}

	@Test
	public void try_to_load_from_non_existing_file() throws Exception {
		final File file = new File( baseFolder, "this will hopefully never exist" );
		assertThat( file ).doesNotExist();
		try {
			persistence.load( file.toURI() );
		} catch ( final Exception e ) {
			Assertions.assertThat( e ).isInstanceOf( ReTestLoadException.class );
		}
	}

}
