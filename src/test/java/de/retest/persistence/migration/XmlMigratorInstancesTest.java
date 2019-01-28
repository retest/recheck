package de.retest.persistence.migration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import de.retest.elementcollection.ElementCollection;
import de.retest.suite.CapturedSuite;
import de.retest.suite.ExecutableSuite;
import de.retest.ui.actions.ActionSequence;

public class XmlMigratorInstancesTest {

	@Test
	public void get_XSLT_for_a_version_that_does_not_have_a_change() throws Exception {
		// Attention: Test will break once we reach version 10000 of the ActionSequence :-)
		assertThat( XmlMigratorInstances.get( "de.retest.ui.actions.ActionSequence" ).getXmlTransformersFor( 9999 ) )
				.containsExactly();
	}

	@Ignore( "Reenable as soon as we have changes again." )
	@Test
	public void get_XSLT_for_a_version_that_has_a_change() throws Exception {
		// We cannot compare the whole list because the test will break as soon as we get the next migration for this type.
		// The first entry is the migration for version 2 although we asked for version 1, so we get an idea that this actually returns a sequence,
		// not just an individual entry.

		//		assertThat(
		//				XmlMigratorInstances.get( "de.retest.ui.actions.ActionSequence" ).getXmlTransformersFor( 1 ).get( 0 ) )
		//						.isInstanceOf( RemoveDescription.class );
	}

	@Test
	public void make_sure_that_all_old_versions_are_still_there() throws Exception {
		// Never remove entries from this list, only add new ones!
		assertThat( XmlMigratorInstances.get( "de.retest.ui.actions.ActionSequence" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.suite.CapturedSuite" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.elementcollection.ElementCollection" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.suite.ExecutableSuite" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.report.ReplayResult" ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( "de.retest.test.Test" ) ).isNotNull();
	}

	@Test
	public void make_sure_that_all_current_versions_are_there() throws Exception {
		// This list must contain all classes that implement Persistable.
		// Exceptions: ReplayResult and StateGraph (who have their own tests).
		assertThat( XmlMigratorInstances.get( ActionSequence.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( CapturedSuite.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( ElementCollection.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( ExecutableSuite.class.getName() ) ).isNotNull();
		assertThat( XmlMigratorInstances.get( de.retest.test.Test.class.getName() ) ).isNotNull();
	}

}
