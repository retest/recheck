package de.retest.recheck.persistence.migration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import de.retest.recheck.persistence.migration.transformers.AddRetestIdTestTransformer;
import de.retest.recheck.suite.CapturedSuite;
import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.actions.ActionSequence;
import de.retest.recheck.ui.descriptors.SutState;

public class XmlMigratorInstancesTest {

	@Test
	public void get_XSLT_for_a_version_that_does_not_have_a_change() throws Exception {
		// Attention: Test will break once we reach version 10000 of the ActionSequence :-)
		assertThat( XmlMigratorInstances.get( "de.retest.recheck.ui.descriptors.SutState" )
				.getXmlTransformersFor( 9999 ) ).containsExactly();
	}
	
	@Test
	public void get_XSLT_for_a_version_that_has_a_change() throws Exception {
		// We cannot compare the whole list because the test will break as soon as we get the next migration for this type.
		// The first entry is the migration for version 2 although we asked for version 1, so we get an idea that this actually returns a sequence,
		// not just an individual entry.

		assertThat(
				XmlMigratorInstances.get( "de.retest.recheck.ui.descriptors.SutState" ).getXmlTransformersFor( 1 ).get( 0 ) )
						.isInstanceOf( AddRetestIdTestTransformer.class );
	}

	@Test
	public void make_sure_that_all_old_versions_are_still_there() throws Exception {
		// Never remove entries from this list, only add new ones!
		assertThat( XmlMigratorInstances.get( "de.retest.recheck.ui.actions.ActionSequence" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.recheck.suite.CapturedSuite" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.recheck.suite.ExecutableSuite" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.recheck.report.ReplayResult" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.recheck.test.Test" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.recheck.ui.descriptors.SutState" ) ).isNotNull();
	}

	@Test
	public void make_sure_that_all_current_versions_are_there() throws Exception {
		// This list must contain all classes that implement Persistable.
		// Exceptions: ReplayResult, who has its own tests.
		assertThat( XmlMigratorInstances.get( ActionSequence.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( CapturedSuite.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( ExecutableSuite.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( de.retest.recheck.test.Test.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( SutState.class.getName() ) ).isNotNull();
	}

}
