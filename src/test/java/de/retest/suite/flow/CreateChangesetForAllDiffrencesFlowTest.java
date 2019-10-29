package de.retest.suite.flow;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

public class CreateChangesetForAllDiffrencesFlowTest {

	private static final String SUITE = "ApplyChangesToStatesFlowTest";
	private static final String TEST = "check_GUI_with_review_license";
	private static final String ACTION = "launch";
	private static final String FILE = "src/test/resources/recheck/" + SUITE + "/" + TEST + "." + ACTION + ".recheck";

	private SutState sutState;
	private File temporaryFile;
	private Persistence<SutState> persistence;

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Rule
	public RestoreSystemProperties recheckDirProp = new RestoreSystemProperties();

	@Before
	public void setUp() throws Exception {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, temp.getRoot().getAbsolutePath().toString() );
		Files.createDirectories( temp.getRoot().toPath().resolve( "src/test/java" ) );
		final PersistenceFactory persistenceFactory =
				new PersistenceFactory( new HashSet<>( Arrays.asList( StdXmlClassesProvider.getXmlDataClasses() ) ) );

		persistence = persistenceFactory.getPersistence();
		final File originalFile = new File( FILE );
		temporaryFile = temp.newFile( "check_GUI_with_review_license.launch.recheck" );

		FileUtils.copyFile( originalFile, temporaryFile );
		sutState = persistence.load( temporaryFile.toURI() );
	}

	@Test
	public void apply_should_throw_exception_whitout_the_stateFilePath() throws Exception {

		final ReviewResult reviewResult = new ReviewResult();
		final SuiteChangeSet changesWhitPathAndDescription = reviewResult.createSuiteChangeSet( SUITE, "" );
		final SuiteChangeSet changesWhitoutAnything = reviewResult.createSuiteChangeSet( SUITE, "" );
		final SuiteChangeSet changesWhitoutStateFilePath = reviewResult.createSuiteChangeSet( SUITE, "" );
		final SuiteChangeSet changesWhitoutDescription = reviewResult.createSuiteChangeSet( SUITE, "" );

		final Element toChange = sutState.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 );

		changesWhitPathAndDescription.createTestChangeSet()
				.createActionChangeSet( ACTION, temporaryFile.getName(), ScreenshotChanges.empty() )
				.getAttributesChanges()
				.add( toChange.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );

		changesWhitoutDescription.createTestChangeSet()
				.createActionChangeSet( null, temporaryFile.getName(), ScreenshotChanges.empty() )
				.getAttributesChanges()
				.add( toChange.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );

		//if createActionChangeSet is called whitout the stateFilePath, a NoStateFileFoundException is thrown
		changesWhitoutStateFilePath.createTestChangeSet()
				.createActionChangeSet( ACTION, null, ScreenshotChanges.empty() ).getAttributesChanges()
				.add( toChange.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );

		changesWhitoutAnything.createTestChangeSet().createActionChangeSet().getAttributesChanges()
				.add( toChange.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );

		assertThatCode( () -> ApplyChangesToStatesFlow.apply( persistence, changesWhitPathAndDescription ) )
				.doesNotThrowAnyException();

		assertThatCode( () -> ApplyChangesToStatesFlow.apply( persistence, changesWhitoutDescription ) )
				.doesNotThrowAnyException();

		assertThatThrownBy( () -> ApplyChangesToStatesFlow.apply( persistence, changesWhitoutStateFilePath ) ) //
				.isInstanceOf( NoGoldenMasterFoundException.class ) //
				.hasMessage( "No Golden Master file(s) with the following name(s) found:\n\tnull" );

		assertThatThrownBy( () -> ApplyChangesToStatesFlow.apply( persistence, changesWhitoutAnything ) ) //
				.isInstanceOf( NoGoldenMasterFoundException.class ) //
				.hasMessage( "No Golden Master file(s) with the following name(s) found:\n\tnull" );

	}
}
