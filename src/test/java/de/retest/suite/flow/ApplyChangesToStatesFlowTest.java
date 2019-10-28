package de.retest.suite.flow;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
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
		final SutState before = persistence.load( temporaryFile.toURI() );

		final ReviewResult reviewResult = new ReviewResult();
		final SuiteChangeSet acceptedChanges = reviewResult.createSuiteChangeSet( SUITE, "" );
		final Element toChange = before.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );
		acceptedChanges.createTestChangeSet()
				.createActionChangeSet( ACTION, temporaryFile.getName(), ScreenshotChanges.empty() )
				.getAttributesChanges()
				.add( toChange.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );

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
		final SutState before1 = persistence.load( temporaryFile1.toURI() );

		FileUtils.copyFile( originalFile, temporaryFile2 );
		final SutState before2 = persistence.load( temporaryFile2.toURI() );

		FileUtils.copyFile( originalFile, temporaryFile3 );
		final SutState before3 = persistence.load( temporaryFile3.toURI() );

		final SuiteChangeSet acceptedChanges =
				buildChanges( temporaryFile1, temporaryFile2, temporaryFile3, before1, before2, before3 );

		temporaryFile1.delete();
		temporaryFile3.delete();

		ApplyChangesToStatesFlow.apply( persistence, acceptedChanges );

		final SutState after2 = persistence.load( temporaryFile2.toURI() );
		final Element changed2 = after2.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );

		assertThat( changed2.getAttributes().get( "enabled" ) ).isEqualTo( true );
	}

	private SuiteChangeSet buildChanges( final File temporaryFile1, final File temporaryFile2,
			final File temporaryFile3, final SutState before1, final SutState before2, final SutState before3 ) {
		final ReviewResult reviewResult = new ReviewResult();
		final SuiteChangeSet acceptedChanges = reviewResult.createSuiteChangeSet( SUITE, "" );
		final Element toChange1 = before1.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );
		final Element toChange2 = before2.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );
		final Element toChange3 = before3.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );
		final TestChangeSet testChangeSet = acceptedChanges.createTestChangeSet();
		testChangeSet.createActionChangeSet( "launch1", temporaryFile1.getName(), ScreenshotChanges.empty() )
				.getAttributesChanges()
				.add( toChange1.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );
		testChangeSet.createActionChangeSet( "launch2", temporaryFile2.getName(), ScreenshotChanges.empty() )
				.getAttributesChanges()
				.add( toChange2.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );
		testChangeSet.createActionChangeSet( "launch3", temporaryFile3.getName(), ScreenshotChanges.empty() )
				.getAttributesChanges()
				.add( toChange3.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );
		return acceptedChanges;
	}
}
