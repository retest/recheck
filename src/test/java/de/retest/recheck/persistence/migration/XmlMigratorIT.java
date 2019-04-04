package de.retest.recheck.persistence.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.migration.transformers.IncompatibleChangesTransformer;
import de.retest.recheck.persistence.xml.ReTestXmlDataContainer;
import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.persistence.xml.util.XmlVersionCheckResult;
import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.util.ApprovalsUtil;
import de.retest.recheck.util.NamedBufferedInputStream;

class XmlMigratorIT {

	@Test
	void migrating_xml_from_retest_v1_should_throw_IncompatibleTypesException() throws Exception {
		final File xmlFile = new File( "src/test/resources/migration/XmlMigratorTest-v1.xml" );
		final NamedBufferedInputStream bin =
				new NamedBufferedInputStream( new FileInputStream( xmlFile ), xmlFile.getName() );
		final XmlVersionCheckResult resultChecker = XmlVersionCheckResult.create( bin );

		assertThat( resultChecker.isCompatible() ).isFalse();

		try {
			XmlMigrator.tryToMigrate( resultChecker, bin );
			fail();
		} catch ( final Throwable t ) {
			assertThat( t.getMessage() ).isEqualTo( IncompatibleChangesTransformer.RETEST_VERSION_2_ERROR_MESSAGE );
		}
	}

	@Test
	void parsing_and_checking_xml_from_retest_v2_0_should_work() throws Exception {
		final File xmlFile = new File( "src/test/resources/migration/XmlMigratorTest-v2.xml" );
		final ExecutableSuite executableSuite = new XmlTransformer()
				.<ReTestXmlDataContainer<ExecutableSuite>> fromXML( new FileInputStream( xmlFile ) ).data();
		final RootElement rootElement =
				executableSuite.getTests().get( 0 ).getInitialState().getRootElements().get( 0 );

		assertThat( rootElement.getIdentifyingAttributes().getPath() ).isEqualTo( "Window[1]" );
		final IdentifyingAttributes firstChildIdentAttributes =
				rootElement.getContainedElements().get( 0 ).getIdentifyingAttributes();
		assertThat( firstChildIdentAttributes.getParentPath().toString() ).isEqualTo( "Window[1]/JRootPane[1]" );
		assertThat( firstChildIdentAttributes.getPath() ).isEqualTo( "Window[1]/JRootPane[1]/JPane[1]" );
	}

	@Test
	void loading_recheck_state_from_v3_1_0_should_work() throws Exception {
		final File xmlFile = new File( "src/test/resources/migration/XmlMigratorTest-retest-v3.1.0.xml" );
		final NamedBufferedInputStream bin =
				new NamedBufferedInputStream( new FileInputStream( xmlFile ), xmlFile.getName() );

		final XmlVersionCheckResult resultChecker = XmlVersionCheckResult.create( bin );

		assertThat( resultChecker.isCompatible() ).isFalse();

		final BufferedInputStream migratedXml = XmlMigrator.tryToMigrate( resultChecker, bin );

		final StringWriter writer = new StringWriter();
		IOUtils.copy( migratedXml, writer, StandardCharsets.UTF_8 );

		ApprovalsUtil.verifyXml( writer.toString() );
	}

	@Disabled( "Unignore as soon as we have migration steps for retest v2." )
	@Test
	void parse_migrated_xml_and_check_result() throws Exception {
		final File xmlFile = new File( "src/test/resources/migration/XmlMigratorTest-v1.xml" );
		final NamedBufferedInputStream bin =
				new NamedBufferedInputStream( new FileInputStream( xmlFile ), xmlFile.getName() );

		final XmlVersionCheckResult resultChecker = XmlVersionCheckResult.create( bin );

		assertThat( resultChecker.isCompatible() ).isFalse();

		final BufferedInputStream migratedXml = XmlMigrator.tryToMigrate( resultChecker, bin );

		final ExecutableSuite executableSuite =
				new XmlTransformer().<ReTestXmlDataContainer<ExecutableSuite>> fromXML( migratedXml ).data();

		final RootElement rootElement =
				executableSuite.getTests().get( 0 ).getInitialState().getRootElements().get( 0 );

		assertThat( rootElement.getIdentifyingAttributes().getPath() ).isEqualTo( "Window" );

		assertThat( rootElement.getContainedElements().get( 0 ).getIdentifyingAttributes().getParentPath().toString() )
				.isEqualTo( "Window/JRootPane_0" );
		assertThat( rootElement.getContainedElements().get( 0 ).getIdentifyingAttributes().getPath() )
				.isEqualTo( "Window/JRootPane_0/JPane_1" );
	}

	@Disabled( "Unignore as soon as we have migration steps for retest v2." )
	@Test
	void migrate_element_collection() throws Exception {
		final File xmlFile = new File( "src/test/resources/migration/recheck_ignore.xml" );
		final NamedBufferedInputStream bin =
				new NamedBufferedInputStream( new FileInputStream( xmlFile ), xmlFile.getName() );

		final XmlVersionCheckResult resultChecker = XmlVersionCheckResult.create( bin );

		assertThat( resultChecker.isCompatible() ).isFalse();

		final BufferedInputStream migratedXml = XmlMigrator.tryToMigrate( resultChecker, bin );

		final StringWriter writer = new StringWriter();
		IOUtils.copy( migratedXml, writer, StandardCharsets.UTF_8 );

		ApprovalsUtil.verifyXml( writer.toString() );
	}

}
