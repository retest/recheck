package de.retest.persistence.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.retest.ioerror.ReTestLoadException;
import de.retest.ioerror.ReTestSaveException;
import de.retest.util.ApprovalsUtil;
import de.retest.util.FileUtil;

public class XmlFolderPersistenceTest {

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	XmlFolderPersistence<TestPersistable> persistence;
	private XmlTransformer xmlTransformer;

	@Before
	public void setUp() {
		xmlTransformer = new XmlTransformer( TestPersistable.class );
		persistence = new XmlFolderPersistence<>( xmlTransformer );
	}

	@Test
	public void simple_save_to_folder() throws Exception {
		final File folder = temp.newFolder();
		final TestPersistable element = new TestPersistable();

		persistence.save( folder.toURI(), element );

		final File retestXmlFile = new File( folder, "retest.xml" );
		assertThat( retestXmlFile ).exists();
		ApprovalsUtil.verifyXml( FileUtil.readFileToString( retestXmlFile ) );
	}

	@Test
	public void try_to_save_to_folder_where_a_file_with_the_same_name_already_exists() throws Exception {
		final File file = temp.newFile();
		final TestPersistable element = new TestPersistable();
		try {
			persistence.save( file.toURI(), element );
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
