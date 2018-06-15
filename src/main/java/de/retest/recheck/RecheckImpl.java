package de.retest.recheck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import de.retest.ExternalExecutor;
import de.retest.elementcollection.ElementCollection;
import de.retest.elementcollection.IgnoredComponents;
import de.retest.persistence.FileNamer;
import de.retest.persistence.Persistence;
import de.retest.persistence.PersistenceFactory;
import de.retest.persistence.bin.KryoPersistence;
import de.retest.persistence.xml.XmlFolderPersistence;
import de.retest.processors.SutExecutor;
import de.retest.report.ActionReplayResult;
import de.retest.report.ReportReplayResult;
import de.retest.report.SuiteReplayResult;
import de.retest.report.TestReplayResult;
import de.retest.suite.ExecutableSuite;
import de.retest.ui.IgnoredTypes;
import de.retest.ui.actions.ActionStateSequence;
import de.retest.ui.descriptors.GroundState;
import de.retest.ui.descriptors.RootElement;
import de.retest.ui.descriptors.SutState;
import de.retest.ui.diff.DifferenceResult;
import de.retest.ui.diff.DurationDifference;
import de.retest.ui.diff.RootElementDifference;
import de.retest.ui.diff.RootElementDifferenceFinder;
import de.retest.ui.diff.StateDifference;
import de.retest.util.FileUtil;
import de.retest.util.MainUtil;
import de.retest.util.SubmittingUncaugthExceptionHandler;
import de.retest.xml.XmlTransformer;

public class RecheckImpl implements Recheck {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( RecheckImpl.class );

	public static final String RETEST_FILE_EXTENSION = "recheck";

	public RecheckImpl() {
		this( new SubmittingUncaugthExceptionHandler() );
	}

	public RecheckImpl( final Thread.UncaughtExceptionHandler excHandler ) {
		this( excHandler, new MavenConformFileNamerStrategy() );
	}

	public RecheckImpl( final Thread.UncaughtExceptionHandler excHandler, final FileNamerStrategy fileNamerStrategy ) {
		MainUtil.initializeRetest();
		SutExecutor.prepareJVM( excHandler );
		IgnoredComponents.loadIgnored(
				new PersistenceFactory( getXmlTransformer() ).<ElementCollection> getPersistence(), getIgnoredTypes() );
		this.fileNamerStrategy = fileNamerStrategy;
		final GroundState groundState = new GroundState();
		final ExecutableSuite execSuite = new ExecutableSuite( groundState, 0, new ArrayList<ActionStateSequence>() );
		execSuite.setName( fileNamerStrategy.getTestClassName() );
		suite = new SuiteReplayResult( execSuite, 0, groundState );
		sutStatePersistence = new XmlFolderPersistence<SutState>( getXmlTransformer() );
		resultPersistence = new KryoPersistence<ReportReplayResult>();
	}

	private IgnoredTypes getIgnoredTypes() {
		return new IgnoredTypes.NoIgnoredTypes();
	}

	private XmlTransformer getXmlTransformer() {
		final Set<Class<?>> xmlDataClasses = new HashSet<Class<?>>();
		xmlDataClasses.add( ReportReplayResult.class );
		xmlDataClasses.add( SutState.class );
		return new XmlTransformer( xmlDataClasses );
	}

	private final SuiteReplayResult suite;
	private TestReplayResult currentTestResult;
	private final FileNamerStrategy fileNamerStrategy;
	private final ImmutableList<RecheckAdapter> adapters =
			ImmutableList.copyOf( ServiceLoader.load( RecheckAdapter.class ).iterator() );
	private final Persistence<SutState> sutStatePersistence;
	private final Persistence<ReportReplayResult> resultPersistence;

	@Override
	public void startTest( final String testName ) {
		currentTestResult = new TestReplayResult( testName, 0 );
	}

	@Override
	public void startTest() {
		startTest( fileNamerStrategy.getTestMethodName() );
	}

	@Override
	public void check( final Object toVerify, final String currentStep ) {
		final RecheckAdapter adapter = getAdapter( toVerify );
		check( toVerify, adapter, currentStep );
	}

	@Override
	public void check( final Object toVerify, final RecheckAdapter adapter, final String currentStep )
			throws IllegalArgumentException {
		final FileNamer fileNamer = fileNamerStrategy.createFileNamer( fileNamerStrategy.getTestClassName()
				+ File.separator + currentTestResult.getName() + "." + currentStep );
		SutState expected = loadExpected( fileNamer.getFile( RETEST_FILE_EXTENSION ) );

		final SutState actual = convert( toVerify, adapter );
		if ( expected == null ) {
			persist( fileNamer.getFile( RETEST_FILE_EXTENSION ), actual );
			expected = new SutState( new ArrayList<RootElement>() );
		}

		final List<RootElementDifference> differences =
				new RootElementDifferenceFinder( adapter.getDefaultValueFinder() )
						.findDifferences( expected.getRootElements(), actual.getRootElements() );
		final DifferenceResult checkResult = new DifferenceResult( actual, differences );
		final ActionReplayResult actionReplayResult = toActionReplayResult( checkResult, currentStep );
		currentTestResult.addAction( actionReplayResult );
	}

	public SutState convert( final Object toVerify, final RecheckAdapter adapter ) {
		final Set<RootElement> converted = adapter.convert( toVerify );
		if ( converted == null || converted.isEmpty() ) {
			throw new IllegalStateException( "Cannot check empty state!" );
		}
		final SutState actual = new SutState( converted );
		return actual;
	}

	protected RecheckAdapter getAdapter( final Object toVerify ) {
		for ( final RecheckAdapter recheckAdapter : adapters ) {
			if ( recheckAdapter.canCheck( toVerify ) ) {
				return recheckAdapter;
			}
		}
		throw new RuntimeException(
				"No ReCheckAdapter registered, that can handle an object of " + toVerify.getClass() );
	}

	private ActionReplayResult toActionReplayResult( final DifferenceResult check, final String currentStep ) {
		final List<RootElementDifference> differences = check.getDifferences();
		if ( differences != null && differences.size() > 0 ) {
			final Optional<DurationDifference> none = Optional.absent();
			final StateDifference stateDifference = new StateDifference( differences, none );
			logger.info( "Found {} differences for step '{}'.", stateDifference.size(), currentStep );
			return new ActionReplayResult( currentStep, "Test API", null, null, null, stateDifference, 0l, null, null );
		}
		logger.info( "Found no differences in step '{}'.", currentStep );
		return new ActionReplayResult( currentStep, "Test API", null, null, null, null, 0l,
				check.getCurrentSutState().getRootElements(), null );
	}

	@Override
	public void capTest() {
		suite.addTest( currentTestResult );
		final TestReplayResult finishedTestResult = currentTestResult;
		currentTestResult = null;
		final Set<Object> uniqueDifferences = finishedTestResult.getUniqueDifferences();
		if ( uniqueDifferences.size() > 0 ) {
			throw new AssertionError( "Found " + finishedTestResult.getDifferencesCount() + " differences, in "
					+ finishedTestResult.getActionReplayResults().size() + " checks of which "
					+ uniqueDifferences.size() + " are unique: " + uniqueDifferences + "\n\nDetails: \n"
					+ finishedTestResult.toString() );
		}
	}

	@Override
	public void cap() {
		if ( currentTestResult != null ) {
			logger.warn( "Test {} was not finished!", currentTestResult.getName() );
			throw new RuntimeException( "Test " + currentTestResult.getName() + " was not finished!" );
		}
		final File file = getResultFile();
		if ( suite.getDifferencesCount() == 0 ) {
			file.delete();
			logger.info( "No differences found!" );
			return;
		}

		persistReplayResult( ReportReplayResult.fromApi( suite ), file );

		launchRetestGui( file );
	}

	public void launchRetestGui( final File file ) {
		try {
			ExternalExecutor.executeExternally( "de.retest.gui.ReTestGui",
					"\"" + file.getCanonicalFile().getAbsolutePath() + "\"" );
		} catch ( final Exception e ) {
			throw new RuntimeException( e );
		}
	}

	protected void persistReplayResult( final ReportReplayResult reportReplayResult, final File file ) {
		logger.info( "Persisting suite result to file {}.", FileUtil.canonicalPathQuietly( file ) );
		try {
			resultPersistence.save( file.toURI(), reportReplayResult );
		} catch ( final IOException e ) {
			throw new RuntimeException( e );
		}
	}

	public File getResultFile() {
		final File file =
				fileNamerStrategy.createFileNamer( fileNamerStrategy.getTestClassName() ).getResultFile( "replay" );
		return file;
	}

	private void persist( final File file, final SutState previous ) {
		try {
			sutStatePersistence.save( file.toURI(), previous );
		} catch ( final IOException e ) {
			throw new RuntimeException( e );
		}
	}

	private SutState loadExpected( final File file ) {
		if ( !file.exists() ) {
			return null;
		}
		try {
			return sutStatePersistence.load( file.toURI() );
		} catch ( final IOException e ) {
			throw new RuntimeException( e );
		}
	}
}
