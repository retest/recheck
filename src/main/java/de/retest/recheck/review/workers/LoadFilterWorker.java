package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ignore.CacheFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.ignore.RecheckIgnoreLocator;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.io.Loaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadFilterWorker {

	private final Counter counter;
	private final Path ignoreFilesBasePath;
	private final RecheckIgnoreLocator locator = new RecheckIgnoreLocator();
	private final RecheckIgnoreLocator jsIgnoreLocator = new RecheckIgnoreLocator( RECHECK_IGNORE_JSRULES );

	public LoadFilterWorker( final Counter counter ) {
		this( counter, null );
	}

	public LoadFilterWorker( final Counter counter, final Path ignoreFilesBasePath ) {
		this.counter = counter;
		this.ignoreFilesBasePath = ignoreFilesBasePath;
	}

	public GlobalIgnoreApplier load() throws IOException {
		final Stream<Filter> allIgnoreFileLines = readIgnoreFileFilters();

		final PersistableGlobalIgnoreApplier ignoreApplier =
				new PersistableGlobalIgnoreApplier( allIgnoreFileLines.collect( Collectors.toList() ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		readIgnoreRuleFileFilters( result );
		return result;
	}

	private Stream<Filter> readIgnoreFileFilters() throws IOException {
		final Optional<Path> projectIgnoreFile = locator.getProjectIgnoreFile();
		final Path userIgnoreFile = locator.getUserIgnoreFile();
		return Stream.concat( Stream.concat( getProjectIgnoreFileFilters( projectIgnoreFile ),
				getUserIgnoreFileFilters( userIgnoreFile ) ), getSuiteIgnoreFileFilters() );
	}

	protected Stream<? extends Filter> getSuiteIgnoreFileFilters() throws IOException {
		if ( ignoreFilesBasePath != null ) {
			final Path suiteIgnoreFile = locator.getSuiteIgnoreFile( ignoreFilesBasePath );
			return getIgnoreFileFilters( suiteIgnoreFile );
		}
		return Stream.empty();
	}

	protected Stream<? extends Filter> getUserIgnoreFileFilters( final Path userIgnoreFile ) throws IOException {
		return getIgnoreFileFilters( userIgnoreFile );
	}

	protected Stream<? extends Filter> getProjectIgnoreFileFilters( final Optional<Path> projectIgnoreFile )
			throws IOException {
		return getIgnoreFileFilters( projectIgnoreFile );
	}

	private void readIgnoreRuleFileFilters( final GlobalIgnoreApplier result ) {
		final Optional<Path> projectIgnoreRuleFile = jsIgnoreLocator.getProjectIgnoreFile();
		final Path userIgnoreRuleFile = jsIgnoreLocator.getUserIgnoreFile();
		addIgnoreRuleFiles( projectIgnoreRuleFile, result );
		addIgnoreRuleFiles( userIgnoreRuleFile, result );

		if ( ignoreFilesBasePath != null ) {
			final Path suiteIgnoreRuleFile = jsIgnoreLocator.getSuiteIgnoreFile( ignoreFilesBasePath );
			addIgnoreRuleFiles( suiteIgnoreRuleFile, result );
		}
	}

	private Stream<? extends Filter> getIgnoreFileFilters( final Optional<Path> ignoreFile ) throws IOException {
		if ( ignoreFile.isPresent() ) {
			return getIgnoreFileFilters( ignoreFile.get() );
		}
		log.info( "Ignoring missing ignore file." );
		return Stream.empty();
	}

	private Stream<? extends Filter> getIgnoreFileFilters( final Path ignoreFile ) throws IOException {
		if ( ignoreFile.toFile().exists() ) {
			log.info( "Reading ignore file from '{}'.", ignoreFile );
			return Loaders.filter().load( ignoreFile, Files.lines( ignoreFile ) );
		}
		log.info( "Ignoring missing ignore file '{}'.", ignoreFile );
		return Stream.empty();
	}

	private void addIgnoreRuleFiles( final Optional<Path> ignoreRuleFile, final GlobalIgnoreApplier result ) {
		if ( ignoreRuleFile.isPresent() ) {
			addIgnoreRuleFiles( ignoreRuleFile.get(), result );
		}
	}

	private void addIgnoreRuleFiles( final Path ignoreRuleFile, final GlobalIgnoreApplier result ) {
		if ( ignoreRuleFile.toFile().exists() ) {
			result.addWithoutCounting( new CacheFilter( new JSFilterImpl( ignoreRuleFile ) ) );
		}
	}

	public Counter getCounter() {
		return counter;
	}
}
