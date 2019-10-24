package de.retest.recheck.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ConfigurationTest {

	private static final String TEST_PROPS_FILE = "target/test-classes/retest.properties";
	private final static String PROPERTY_KEY_IN_DEFAULT_PROPS = "de.retest.vmArgs";

	static Properties ORIGIN_PROPS = System.getProperties();

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.setProperties( ORIGIN_PROPS );
	}

	Properties testSystemProps = new Properties();

	@After
	public void tearDown() throws Exception {
		Configuration.resetRetest();
		assertThat( System.getProperties() ).isEqualTo( testSystemProps );
	}

	@Before
	public void setUp() throws Exception {
		System.setProperties( testSystemProps );
		System.setProperty( "user.dir", ORIGIN_PROPS.getProperty( "user.dir" ) );
	}

	@Test
	public void ensureLoaded_change_properties() throws Exception {
		final Properties origin = System.getProperties();

		Configuration.ensureLoaded();

		assertThat( System.getProperties() ).isNotEqualTo( origin );
		assertThat( System.getProperties().size() ).isNotEqualTo( origin.size() );
	}

	@Test
	public void ensureLoaded_add_default_properties() throws Exception {
		Configuration.ensureLoaded();

		assertThat( System.getProperties().getProperty( PROPERTY_KEY_IN_DEFAULT_PROPS ) ).isNotNull();
	}

	@Test
	public void ensureLoaded_multiple_times_change_nothing() throws Exception {
		Configuration.ensureLoaded();
		final Properties origin = System.getProperties();

		Configuration.ensureLoaded();
		Configuration.ensureLoaded();
		Configuration.ensureLoaded();

		assertThat( System.getProperties() ).isEqualTo( origin );
		assertThat( System.getProperties().size() ).isEqualTo( origin.size() );
	}

	// correct order of property lists

	@Test
	public void runtime_props_are_returned_correctly() throws Exception {
		Configuration.ensureLoaded();
		System.setProperty( "test.prop", "test.val" );

		final String expected = System.getProperty( "test.prop" );

		assertThat( expected ).isEqualTo( "test.val" );
	}

	@Test
	public void runtime_props_are_higher_than_console_props() throws Exception {
		// set system prop before Configuration init is similar than set by console
		System.setProperty( "test.prop", "should_be_overwritten_by_runtime" );

		Configuration.ensureLoaded();
		System.setProperty( "test.prop", "runtime_value" );

		final String expected = System.getProperty( "test.prop" );

		assertThat( expected ).isEqualTo( "runtime_value" );
	}

	@Test
	public void console_props_are_higher_than_default_props() throws Exception {
		// set system prop before Configuration init is similar than set by console
		System.setProperty( PROPERTY_KEY_IN_DEFAULT_PROPS, "should_be_not_overwritten_by_default" );

		Configuration.ensureLoaded();
		final String expected = System.getProperty( PROPERTY_KEY_IN_DEFAULT_PROPS );

		assertThat( expected ).isEqualTo( "should_be_not_overwritten_by_default" );
	}

	@Test
	public void runtime_props_are_higher_than_default_props() throws Exception {
		Configuration.ensureLoaded();
		System.setProperty( PROPERTY_KEY_IN_DEFAULT_PROPS, "runtime_value" );
		final String expected = System.getProperty( PROPERTY_KEY_IN_DEFAULT_PROPS );
		assertThat( expected ).isEqualTo( "runtime_value" );
	}

	// find retest.properties correctly

	@Test
	public void ensureLoaded_respect_config_file_prop() throws Exception {
		System.setProperty( de.retest.recheck.Properties.CONFIG_FILE_PROPERTY, TEST_PROPS_FILE );

		Configuration.ensureLoaded();

		assertThat( System.getProperty( de.retest.recheck.Properties.CONFIG_FILE_PROPERTY ) )
				.isEqualTo( TEST_PROPS_FILE );
		assertThat( System.getProperty( "de.retest.test.config.simple" ) ).isEqualTo( "readed_from_file" );
	}

	@Test
	public void ensureLoaded_search_config_file_in_execDir() throws Exception {
		System.setProperty( "user.dir", new File( TEST_PROPS_FILE ).getParentFile().getAbsolutePath() );

		Configuration.ensureLoaded();

		assertThat( System.getProperty( "de.retest.test.config.simple" ) ).isEqualTo( "readed_from_file" );
	}

	@Test
	public void ensureLoaded_search_config_file_in_workDir() throws Exception {
		System.setProperty( de.retest.recheck.Properties.WORK_DIRECTORY, new File( TEST_PROPS_FILE ).getParent() );

		Configuration.ensureLoaded();

		assertThat( System.getProperty( "de.retest.test.config.simple" ) ).isEqualTo( "readed_from_file" );
	}

	// read props with slashes correctly

	@Test
	public void should_read_backslashes() throws IOException {
		Configuration.setConfigFile( new File( TEST_PROPS_FILE ) );

		assertThat( System.getProperty( "de.retest.test.config.backslash" ) ).isEqualTo( "\\doh\\path" );
	}

	@Test
	public void should_read_win_path() throws IOException {
		Configuration.setConfigFile( new File( TEST_PROPS_FILE ) );

		assertThat( System.getProperty( "de.retest.test.config.winpath" ) )
				.isEqualTo( "C:\\Programme (x86)\\retest\\" );
	}

	@Test
	public void should_read_slashes() throws IOException {
		Configuration.setConfigFile( new File( TEST_PROPS_FILE ) );

		assertThat( System.getProperty( "de.retest.test.config.slash" ) ).isEqualTo( "/doh/path" );
	}
}
