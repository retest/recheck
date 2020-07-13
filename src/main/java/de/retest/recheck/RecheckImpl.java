package de.retest.recheck;

import static de.retest.recheck.util.FileUtil.normalize;

import java.awt.HeadlessException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.execution.RecheckAdapters;
import de.retest.recheck.execution.RecheckDifferenceFinder;
import de.retest.recheck.persistence.CloudPersistence;
import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.persistence.RecheckSutState;
import de.retest.recheck.persistence.RecheckTestReportUtil;
import de.retest.recheck.printer.TestReplayResultPrinter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.report.TestReportFilter;
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

	private static final Logger logger = LoggerFactory.getLogger( RecheckImpl.class );

	private final CapWarner capWarner = new CapWarner();
	private final SuiteReplayResult suite;
	private final RecheckOptions options;
	private final String suiteName;

	private TestReplayResult currentTestResult;

	private final Map<String, DefaultValueFinder> usedFinders = new HashMap<>();
	private final TestReplayResultPrinter printer = new TestReplayResultPrinter( usedFinders::get );

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
		Runtime.getRuntime().addShutdownHook( capWarner );
		this.options = options;
		suiteName = options.getNamingStrategy().getSuiteName();
		suite = SuiteAggregator.getInstance().getSuite( suiteName,
				options.getProjectLayout().getTestSourcesRoot().orElse( null ) );

		if ( options.isReportUploadEnabled() ) {
			try {
				Rehub.authenticate();
			} catch ( final HeadlessException e ) {
				throw new AssertionError( "Please set a valid recheck API key within the environment, using '"
						+ CloudPersistence.RECHECK_API_KEY + "'." );
			}
		}
	}

	@Override
	public void startTest() {
		if ( options.getFileNamerStrategy() != null ) {
			startTest( options.getFileNamerStrategy().getTestMethodName() );
		} else {
			startTest( options.getNamingStrategy().getTestName() );
		}
	}

	@Override
	public void startTest( final String testName ) {
		currentTestResult = new TestReplayResult( testName, 0 );
	}

	@Override
	public void check( final Object toVerify, final String currentStep ) {
		check( toVerify, RecheckAdapters.findAdapterFor( toVerify, options ), currentStep );
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
		final DefaultValueFinder defaultFinder = getFinder( adapter, currentStep );

		final File file = getGoldenMasterFile( currentStep );

		final SutState actual = RecheckSutState.convert( toVerify, adapter );
		final SutState expected = loadExpected( file );
		if ( expected == null ) {
			createNew( file, actual );
			return new NoGoldenMasterActionReplayResult( currentStep, actual, file.getPath() );
		}
		final RecheckDifferenceFinder finder =
				new RecheckDifferenceFinder( defaultFinder, currentStep, file.getPath() );

		final ActionReplayResult actionReplayResult = finder.findDifferences( expected, actual );
		if ( actionReplayResult.hasDifferences() ) {
			adapter.notifyAboutDifferences( actionReplayResult );
		}
		return actionReplayResult;
	}

	protected DefaultValueFinder getFinder( final RecheckAdapter adapter, final String currentStep ) {
		final DefaultValueFinder defaultFinder = adapter.getDefaultValueFinder();
		usedFinders.put( currentStep, defaultFinder );
		return defaultFinder;
	}

	protected File getGoldenMasterFile( final String currentStep ) {
		// This is the legacy impl ... remove at some point
		if ( options.getFileNamerStrategy() != null ) {
			final String name =
					suiteName + File.separator + currentTestResult.getName() + "." + normalize( currentStep );
			final FileNamer fileNamer = options.getFileNamerStrategy().createFileNamer( name );
			return fileNamer.getFile( RecheckProperties.GOLDEN_MASTER_FILE_EXTENSION );
		}
		return options.getProjectLayout()
				.getGoldenMaster( suiteName, currentTestResult.getName(), normalize( currentStep ) ).toFile();
	}

	@Override
	public SutState loadExpected( final File file ) {
		return RecheckSutState.loadExpected( file );
	}

	@Override
	public SutState createNew( final File file, final SutState actual ) {
		return RecheckSutState.createNew( file, actual );
	}

	protected TestReplayResult capTestSilently() {
		suite.addTest( currentTestResult );
		final TestReportFilter testReportFilter = new TestReportFilter( options.getFilter() );
		final TestReplayResult filteredTestResult = testReportFilter.filter( currentTestResult );
		currentTestResult = null;
		return filteredTestResult;
	}

	@Override
	public void capTest() {
		final TestReplayResult filteredTestResult = capTestSilently();
		final Set<LeafDifference> uniqueDifferences = filteredTestResult.getDifferences();
		if ( !uniqueDifferences.isEmpty() ) {
			logger.warn( "Found {} not ignored difference(s) in test '{}'.", uniqueDifferences.size(),
					filteredTestResult.getName() );
			final RecheckCapMessage msg =
					new RecheckCapMessage( suiteName, filteredTestResult, printer, getResultFile() );
			throw new AssertionError( msg );
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
		// legacy
		if ( options.getFileNamerStrategy() != null ) {
			return options.getFileNamerStrategy().createFileNamer( suiteName )
					.getResultFile( RecheckProperties.TEST_REPORT_FILE_EXTENSION );
		}
		return options.getProjectLayout().getReport( suiteName ).toFile();
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

}
