package de.retest.recheck;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.execution.RecheckAdapters;
import de.retest.recheck.execution.RecheckDifferenceFinder;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.persistence.RecheckReplayResultUtil;
import de.retest.recheck.persistence.RecheckSutState;
import de.retest.recheck.printer.TestReplayResultPrinter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.LeafDifference;

public class RecheckImpl implements Recheck, SutStateLoader {

	private static final String WORKSPACE_DEFAULT = "src/test/resources/retest/";
	private static final String CONFIG_PATH = WORKSPACE_DEFAULT + Properties.RETEST_PROPERTIES_FILE_NAME;

	private static final Logger logger = LoggerFactory.getLogger( RecheckImpl.class );

	private final CapWarner capWarner = new CapWarner();
	private final SuiteReplayResult suite;
	private final FileNamerStrategy fileNamerStrategy;
	private final String suiteName;

	private TestReplayResult currentTestResult;

	private final Map<String, DefaultValueFinder> usedFinders = new HashMap<>();
	private final TestReplayResultPrinter printer;
	private final GlobalIgnoreApplier ignoreApplier;

	public RecheckImpl() {
		this( new RecheckOptions() );
	}

	public RecheckImpl( final RecheckOptions options ) {
		ProjectConfiguration.getInstance().ensureProjectConfigurationInitialized();
		ensureConfigurationInitialized();
		Runtime.getRuntime().addShutdownHook( capWarner );
		fileNamerStrategy = options.getFileNamerStrategy();
		suiteName = options.getSuiteName();
		suite = ReplayResultProvider.getInstance().getSuite( suiteName );
		ignoreApplier = RecheckIgnoreUtil.loadRecheckIgnore();
		printer = new TestReplayResultPrinter( usedFinders::get, ignoreApplier );
	}

	private static void ensureConfigurationInitialized() {
		if ( System.getProperty( Properties.PROP_CONFIG_FILE_PATH ) == null ) {
			System.setProperty( Properties.PROP_CONFIG_FILE_PATH, CONFIG_PATH );
		}
	}

	@Override
	public void startTest() {
		startTest( fileNamerStrategy.getTestMethodName() );
	}

	@Override
	public void startTest( final String testName ) {
		currentTestResult = new TestReplayResult( testName, 0 );
	}

	@Override
	public void check( final Object toVerify, final String currentStep ) {
		check( toVerify, RecheckAdapters.findAdapterFor( toVerify ), currentStep );
	}

	@Override
	public void check( final Object toVerify, final RecheckAdapter adapter, final String currentStep ) {
		if ( currentTestResult == null ) {
			logger.warn( "Please call 'startTest()' before performing a check." );
			startTest();
		}

		final ActionReplayResult actionReplayResult = createActionReplayResult( toVerify, adapter, currentStep );

		currentTestResult.addAction( actionReplayResult );
	}

	protected ActionReplayResult createActionReplayResult( final Object toVerify, final RecheckAdapter adapter,
			final String currentStep ) {
		final DefaultValueFinder defaultFinder = adapter.getDefaultValueFinder();
		usedFinders.put( currentStep, defaultFinder );

		final FileNamer fileNamer = createFileName( currentStep );
		final File file = fileNamer.getFile( Properties.RECHECK_FILE_EXTENSION );

		final SutState actual = RecheckSutState.convert( toVerify, adapter );
		final SutState expected = loadExpected( file );
		if ( expected == null ) {
			createNew( file, actual );
			return new NoRecheckFileActionReplayResult( currentStep, actual );
		}
		final RecheckDifferenceFinder finder =
				new RecheckDifferenceFinder( adapter.getDefaultValueFinder(), currentStep, file.getPath() );

		return finder.findDifferences( actual, expected );
	}

	@Override
	public SutState loadExpected( final File file ) {
		return RecheckSutState.loadExpected( file );
	}

	@Override
	public SutState createNew( final File file, final SutState actual ) {
		return RecheckSutState.createNew( file, actual );
	}

	private FileNamer createFileName( final String currentStep ) {
		final String name = suiteName + File.separator + currentTestResult.getName() + "." + currentStep;
		return fileNamerStrategy.createFileNamer( name );
	}

	@Override
	public void capTest() {
		suite.addTest( currentTestResult );
		final TestReplayResult finishedTestResult = currentTestResult;
		currentTestResult = null;
		final Set<LeafDifference> uniqueDifferences = finishedTestResult.getDifferences( ignoreApplier );
		if ( !uniqueDifferences.isEmpty() ) {
			final String message = finishedTestResult.hasNoRecheckFiles() ? getNoRecheckFilesErrorMessage()
					: getDifferencesErrorMessage( finishedTestResult );
			throw new AssertionError( message );
		}
	}

	@Override
	public void cap() {
		capWarner.disarm();
		try {
			if ( currentTestResult != null ) {
				logger.warn(
						"Test {} was not finished. You should call 'capTest()' after your test, to have it fail on changes!",
						currentTestResult.getName() );
				capTest();
			}
		} finally {
			final File file = getResultFile();
			RecheckReplayResultUtil.persist( suite, file );
		}
	}

	public File getResultFile() {
		return fileNamerStrategy.createFileNamer( suiteName ).getResultFile( Properties.REPORT_FILE_EXTENSION );
	}

	private class CapWarner extends Thread {
		private volatile boolean armed = true;

		@Override
		public void run() {
			if ( !armed ) {
				return;
			}
			if ( currentTestResult != null ) {
				logger.warn( "Test {} was not finished!", currentTestResult.getName() );
			}
			logger.warn( "You should call 'cap()' after your test '{}' has finished, to persist test results.",
					suiteName );
		}

		public void disarm() {
			armed = false;
			Runtime.getRuntime().removeShutdownHook( this );
		}
	}

	private String getNoRecheckFilesErrorMessage() {
		return "'" + suiteName + "':\n" + NoRecheckFileActionReplayResult.MSG_LONG;
	}

	private String getDifferencesErrorMessage( final TestReplayResult finishedTestResult ) {
		return "\nA detailed report will be created at \'" + getResultFile() + "\'. " //
				+ "You can review the details by using our GUI from https://retest.de/review/.\n" //
				+ "\nThe following differences have been found in \'" + suiteName //
				+ "\'(with " + finishedTestResult.getActionReplayResults().size() + " check(s)):" //
				+ "\n" + printer.toString( finishedTestResult );
	}
}
