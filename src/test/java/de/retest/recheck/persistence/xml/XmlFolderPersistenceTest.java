package de.retest.recheck.persistence.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

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

@ExtendWith( TempDirectory.class )
public class XmlFolderPersistenceTest {

	File baseFolder;
	XmlFolderPersistence<TestPersistable> persistence;
	private XmlTransformer xmlTransformer;

	@BeforeEach
	public void setUp( @TempDir final Path temp ) {
		baseFolder = temp.toFile();
		xmlTransformer = new XmlTransformer( TestPersistable.class );
		persistence = new XmlFolderPersistence<>( xmlTransformer );
	}

	@Test
	public void simple_save_to_folder() throws Exception {
		final TestPersistable element = new TestPersistable();

		persistence.save( baseFolder.toURI(), element );

		final File retestXmlFile = new File( baseFolder, "retest.xml" );
		assertThat( retestXmlFile ).exists();
		ApprovalsUtil.verifyXml( FileUtil.readFileToString( retestXmlFile ) );
	}

	@Test
	public void try_to_save_to_folder_where_a_file_with_the_same_name_already_exists() throws Exception {
		final TestPersistable element = new TestPersistable();
		try {
			persistence.save( baseFolder.toURI(), element );
		} catch ( final Exception e ) {
			assertThat( e ).isInstanceOf( ReTestSaveException.class );
		}
	}

	@Test
	public void simple_load_from_folder() throws Exception {
		final URI identifier = FileUtil.getFileUriForClasspathRelativPath( "/persistence/simple_folder_persisted" );

		final TestPersistable element = persistence.load( identifier );

		Assertions.assertThat( element ).isNotNull();
	}

	@Test
	public void try_to_load_from_non_existing_file() throws Exception {
		final File file = new File( "this will hopefully never exist" );
		assertThat( file ).doesNotExist();
		try {
			persistence.load( file.toURI() );
		} catch ( final Exception e ) {
			Assertions.assertThat( e ).isInstanceOf( ReTestLoadException.class );
		}
	}

}
