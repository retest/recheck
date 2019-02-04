package de.retest.suite.flow;


import static de.retest.Properties.WORK_DIRECTORY;
import static de.retest.persistence.RecheckStateFileProviderImpl.RECHECK_PROJECT_ROOT;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.Properties;
import de.retest.configuration.Configuration;
import de.retest.persistence.Persistence;
import de.retest.persistence.PersistenceFactory;
import de.retest.persistence.xml.util.StdXmlClassesProvider;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.SutState;
import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.review.ReviewResult;
import de.retest.ui.review.SuiteChangeSet;
import de.retest.util.junit.vintage.SystemProperty;

public class ApplyChangesToStatesFlowTest {

	private static final Logger logger = LoggerFactory.getLogger( ApplyChangesToStatesFlowTest.class );

	private static final String SUITE = "ApplyChangesToStatesFlowTest";
	private static final String TEST = "check_GUI_with_review_license";
	private static final String ACTION = "launch";
	private static final String FILE = "target/test-classes/recheck/" + SUITE + "/" + TEST + "." + ACTION + ".recheck";

	@Rule
	public SystemProperty workDirProp = new SystemProperty( WORK_DIRECTORY );

	@Rule
	public SystemProperty recheckDirProp = new SystemProperty( RECHECK_PROJECT_ROOT );

	@Before
	public void setUp() {
		Configuration.resetRetest();
		workDirProp.setValue( "target/test-classes" );
		logger.info( "Workspace now pointing to {}.", Configuration.getRetestWorkspace() );
		recheckDirProp.setValue( Properties.getReTestInstallDir().toString() );
	}

	@Test
	public void apply_should_apply_changes_to_states() throws Exception {
		final PersistenceFactory persistenceFactory =
				new PersistenceFactory( new HashSet<>( Arrays.asList( StdXmlClassesProvider.getXmlDataClasses() ) ) );
		final Persistence<SutState> persistence = persistenceFactory.getPersistence();
		final File file = new File( FILE );
		final SutState before = persistence.load( file.toURI() );
		logger.debug( "Before loaded" );

		final ReviewResult reviewResult = new ReviewResult();
		final SuiteChangeSet acceptedChanges = reviewResult.createSuiteChangeSet( SUITE, "" );
		final Element toChange = before.getRootElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 )
				.getContainedElements().get( 0 );
		acceptedChanges.createTestChangeSet().createActionChangeSet( ACTION, file.getPath() ).getAttributesChanges()
				.add( toChange.getIdentifyingAttributes(), new AttributeDifference( "enabled", "false", "true" ) );
		logger.debug( "AcceptedChanges created." );

		ApplyChangesToStatesFlow.apply( persistence, acceptedChanges );
		final SutState after = persistence.load( file.toURI() );
		final Element changed = after.getRootElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements()
				.get( 0 ).getContainedElements().get( 0 ).getContainedElements().get( 0 ).getContainedElements()
				.get( 0 );

		assertThat( changed.getAttributes().get( "enabled" ) ).isEqualTo( true );

		// even more advanced: check if difference between before and after is acceptedChanges!
	}
}
