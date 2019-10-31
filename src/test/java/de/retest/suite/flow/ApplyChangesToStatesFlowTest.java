package de.retest.suite.flow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.persistence.NoGoldenMasterFoundException;
import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.persistence.PersistenceFactory;
import de.retest.recheck.persistence.xml.util.StdXmlClassesProvider;
import de.retest.recheck.suite.flow.ApplyChangesToStatesFlow;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.review.ReviewResult;
import de.retest.recheck.ui.review.ScreenshotChanges;
import de.retest.recheck.ui.review.SuiteChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;

public class ApplyChangesToStatesFlowTest {

	private static final String SUITE = "ApplyChangesToStatesFlowTest";
	private static final String TEST = "check_GUI_with_review_license";
	private static final String ACTION = "launch";
	private static final String FILE = "src/test/resources/recheck/" + SUITE + "/" + TEST + "." + ACTION + ".recheck";

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Rule
	public final RestoreSystemProperties recheckDirProp = new RestoreSystemProperties();

	@Before
	public void setUp() throws Exception {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, temp.getRoot().getAbsolutePath().toString() );
		Files.createDirectories( temp.getRoot().toPath().resolve( "src/main/java" ) );
		Files.createDirectories( temp.getRoot().toPath().resolve( "src/test/java" ) );
	}

	@Test
	public void apply_should_apply_changes_to_states() throws Exception {
		final PersistenceFactory persistenceFactory =
				new PersistenceFactory( new HashSet<>( Arrays.asList( StdXmlClassesProvider.getXmlDataClasses() ) ) );
		final Persistence<SutState> persistence = persistenceFactory.getPersistence();

		final File originalFile = new File( FILE );
		final File temporaryFile = temp.newFile( "check_GUI_with_review_license.launch.recheck" );

		FileUtils.copyFile( originalFile, temporaryFile );

		final ReviewResult reviewResult = new ReviewResult();
		final SuiteChangeSet acceptedChanges = reviewResult.createSuiteChangeSet( SUITE, "" );
		final TestChangeSet testChangeSet = acceptedChanges.createTestChangeSet();
		buildChanges( persistence, testChangeSet, temporaryFile, ACTION );

		ApplyChangesToStatesFlow.apply( persistence, acceptedChanges );

		final SutState after = persistence.load( temporaryFile.toURI() );
		final Element changed = after.getRootElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements()
				.get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements()
				.get( 0 );

		assertThat( changed.getAttributes().get( "enabled" ) ).isEqualTo( true );

		// even more advanced: check if difference between before and after is acceptedChanges!
	}

	@Test
	public void apply_should_not_fail_immediatly_when_a_gm_cannot_be_found() throws Exception {
		final PersistenceFactory persistenceFactory =
				new PersistenceFactory( new HashSet<>( Arrays.asList( StdXmlClassesProvider.getXmlDataClasses() ) ) );
		final Persistence<SutState> persistence = persistenceFactory.getPersistence();

		final File originalFile = new File( FILE );
		final File temporaryFile1 = temp.newFile( "check_GUI_with_review_license.launch1.recheck" );
		final File temporaryFile2 = temp.newFile( "check_GUI_with_review_license.launch2.recheck" );
		final File temporaryFile3 = temp.newFile( "check_GUI_with_review_license.launch3.recheck" );

		FileUtils.copyFile( originalFile, temporaryFile1 );
		FileUtils.copyFile( originalFile, temporaryFile2 );
		FileUtils.copyFile( originalFile, temporaryFile3 );

		final ReviewResult reviewResult = new ReviewResult();
		final SuiteChangeSet acceptedChanges = reviewResult.createSuiteChangeSet( SUITE, "" );
		final TestChangeSet testChangeSet = acceptedChanges.createTestChangeSet();
		buildChanges( persistence, testChangeSet, temporaryFile1, "launch1" );
		buildChanges( persistence, testChangeSet, temporaryFile2, "launch2" );
		buildChanges( persistence, testChangeSet, temporaryFile3, "launch3" );

		temporaryFile1.delete();
		temporaryFile3.delete();

		assertThatThrownBy( () -> ApplyChangesToStatesFlow.apply( persistence, acceptedChanges ) ) //
				.isInstanceOf( NoGoldenMasterFoundException.class ) //
				.hasMessage( "The following Golden Master(s) cannot be found:\n" //
						+ "\t- check_GUI_with_review_license.launch1.recheck\n"//
						+ "\t- check_GUI_with_review_license.launch3.recheck" );

		final SutState after2 = persistence.load( temporaryFile2.toURI() );
		final Element changed2 = after2.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );

		assertThat( changed2.getAttributes().get( "enabled" ) ).isEqualTo( true );
	}

	private void buildChanges( final Persistence<SutState> persistence, final TestChangeSet testChangeSet,
			final File temporaryFile, final String actionChangeSetDescription ) throws IOException {
		final SutState before = persistence.load( temporaryFile.toURI() );
		final Element toChange = before.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );
		testChangeSet
				.createActionChangeSet( actionChangeSetDescription, temporaryFile.getName(), ScreenshotChanges.empty() )
				.getAttributesChanges()
				.add( toChange.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );
	}
}
