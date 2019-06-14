package de.retest.recheck;

import static de.retest.recheck.util.FileUtil.normalize;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.execution.RecheckAdapters;
import de.retest.recheck.execution.RecheckDifferenceFinder;
import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.persistence.RecheckSutState;
import de.retest.recheck.persistence.RecheckTestReportUtil;
import de.retest.recheck.printer.TestReplayResultPrinter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.LeafDifference;

/**
 * Base implementation to use with e.g. recheck-web (https://github.com/retest/recheck-web) or any other recheck
 * implementation. Defines the principle test API and implements functionality on the basis of interfaces.
 *
 *
 * Principle usage pattern (e.g. in the context of JUnit or TestNG):
 *
 * <pre>
 * Recheck re = new RecheckImpl();
 * re.startTest();
 * re.check( yourObjectToCheck, "unique identifier" ); // check, but do not fail yet
 * re.capTest(); // throw AssertionError on difference
 * re.cap(); // produce the report file
 * </pre>
 */
public class RecheckImpl implements Recheck, SutStateLoader {

	private static final String PROJECT_DEFAULT = "src/test/resources/retest/";
	private static final String CONFIG_PATH = PROJECT_DEFAULT + Properties.RETEST_PROPERTIES_FILE_NAME;

	private static final Logger logger = LoggerFactory.getLogger( RecheckImpl.class );

	private final CapWarner capWarner = new CapWarner();
	private final SuiteReplayResult suite;
	private final FileNamerStrategy fileNamerStrategy;
	private final String suiteName;

	private TestReplayResult currentTestResult;

	private final Map<String, DefaultValueFinder> usedFinders = new HashMap<>();
	private final TestReplayResultPrinter printer;
	private final Filter filter;

	/**
	 * Constructor that works purely with defaults. Default {@link FileNamerStrategy} assumes being called from within a
	 * JUnit or TestNG test. If you want to use recheck outside of a testing context, please use
	 * {@link RecheckImpl#RecheckImpl(RecheckOptions)} with a different {@link FileNamerStrategy} for naming the Golden
	 * Master files.
	 */
	public RecheckImpl() {
		this( RecheckOptions.builder().build() );
	}

	public RecheckImpl( final RecheckOptions options ) {
		ProjectConfiguration.getInstance().ensureProjectConfigurationInitialized();
		ensureConfigurationInitialized();
		Runtime.getRuntime().addShutdownHook( capWarner );
		fileNamerStrategy = options.getFileNamerStrategy();
		suiteName = options.getSuiteName();
		suite = SuiteAggregator.getInstance().getSuite( suiteName );

		final GlobalIgnoreApplier globalIgnoreApplier = RecheckIgnoreUtil.loadRecheckIgnore();
		final GlobalIgnoreApplier suiteIgnoreApplier = RecheckIgnoreUtil.loadRecheckSuiteIgnore( getSuitePath() );
		filter = new CompoundFilter( Arrays.asList( globalIgnoreApplier, suiteIgnoreApplier ) );

		printer = new TestReplayResultPrinter( usedFinders::get, filter );

		if ( isRehubEnabled( options ) ) {
			Rehub.init();
		}
	}

	private boolean isRehubEnabled( final RecheckOptions options ) {
		return options.isRehubEnabled() || Boolean.getBoolean( Properties.REHUB_ENABLED );
	}

	private static void ensureConfigurationInitialized() {
		if ( System.getProperty( Properties.CONFIG_FILE_PROPERTY ) == null ) {
			System.setProperty( Properties.CONFIG_FILE_PROPERTY, CONFIG_PATH );
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
		final File file = fileNamer.getFile( Properties.GOLDEN_MASTER_FILE_EXTENSION );

		final SutState actual = RecheckSutState.convert( toVerify, adapter );
		final SutState expected = loadExpected( file );
		if ( expected == null ) {
			createNew( file, actual );
			return new NoGoldenMasterActionReplayResult( currentStep, actual, file.getPath() );
		}
		final RecheckDifferenceFinder finder =
				new RecheckDifferenceFinder( adapter.getDefaultValueFinder(), currentStep, file.getPath() );

		return finder.findDifferences( actual, expected );
	}

	private File getSuitePath() {
		return fileNamerStrategy.createFileNamer( suiteName ).getFile( "" );
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
		final String name = suiteName + File.separator + currentTestResult.getName() + "." + normalize( currentStep );
		return fileNamerStrategy.createFileNamer( name );
	}

	@Override
	public void capTest() {
		suite.addTest( currentTestResult );
		final TestReplayResult finishedTestResult = currentTestResult;
		currentTestResult = null;
		final Set<LeafDifference> uniqueDifferences = finishedTestResult.getDifferences( filter );
		logger.info( "Found {} not ignored differences in test {}.", uniqueDifferences.size(),
				finishedTestResult.getName() );
		if ( !uniqueDifferences.isEmpty() ) {
			final String message =
					finishedTestResult.hasNoGoldenMaster() ? getNoGoldenMasterErrorMessage( finishedTestResult )
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
			RecheckTestReportUtil.persist( suite, file );
		}
	}

	public File getResultFile() {
		return fileNamerStrategy.createFileNamer( suiteName ).getResultFile( Properties.TEST_REPORT_FILE_EXTENSION );
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

	private String getNoGoldenMasterErrorMessage( final TestReplayResult finishedTestResult ) {
		final String goldenMasterPath = finishedTestResult.getActionReplayResults().stream() //
				.map( ActionReplayResult::getGoldenMasterPath ) //
				.collect( Collectors.joining( "\n" ) );
		return "'" + suiteName + "':\n" + NoGoldenMasterActionReplayResult.MSG_LONG + "\n" + goldenMasterPath;
	}

	private String getDifferencesErrorMessage( final TestReplayResult finishedTestResult ) {
		final int numChecks = finishedTestResult.getActionReplayResults().size();
		final String allDiffs = printer.toString( finishedTestResult );
		final String reportPath = getResultFile().getAbsolutePath();
		return "A detailed report will be created at '" + reportPath + "'. " //
				+ "You can review the details by using our CLI (https://github.com/retest/recheck.cli/) or GUI (https://retest.de/review/).\n" //
				+ "\n" //
				+ numChecks + " check(s) in '" + suiteName + "' found the following difference(s):\n" //
				+ allDiffs;
	}
}
