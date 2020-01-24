package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ignore.CacheFilter;
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
		final Stream<String> allIgnoreFileLines = readIgnoreFileLines();

		final PersistableGlobalIgnoreApplier ignoreApplier =
				new PersistableGlobalIgnoreApplier( allIgnoreFileLines.collect( Collectors.toList() ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		readIgnoreRuleFileLines( result );
		return result;
	}

	private Stream<String> readIgnoreFileLines() throws IOException {
		final Optional<Path> projectIgnoreFile = locator.getProjectIgnoreFile();
		final Path userIgnoreFile = locator.getUserIgnoreFile();
		return Stream.concat( Stream.concat( getProjectIgnoreFileLines( projectIgnoreFile ),
				getUserIgnoreFileLines( userIgnoreFile ) ), getSuiteIgnoreFileLines() );
	}

	protected Stream<String> getSuiteIgnoreFileLines() throws IOException {
		if ( ignoreFilesBasePath != null ) {
			final Path suiteIgnoreFile = locator.getSuiteIgnoreFile( ignoreFilesBasePath );
			return getIgnoreFileLines( suiteIgnoreFile );
		}
		return Stream.empty();
	}

	protected Stream<String> getUserIgnoreFileLines( final Path userIgnoreFile ) throws IOException {
		return getIgnoreFileLines( userIgnoreFile );
	}

	protected Stream<String> getProjectIgnoreFileLines( final Optional<Path> projectIgnoreFile ) throws IOException {
		return getIgnoreFileLines( projectIgnoreFile );
	}

	private void readIgnoreRuleFileLines( final GlobalIgnoreApplier result ) {
		final Optional<Path> projectIgnoreRuleFile = jsIgnoreLocator.getProjectIgnoreFile();
		final Path userIgnoreRuleFile = jsIgnoreLocator.getUserIgnoreFile();
		addIgnoreRuleFiles( projectIgnoreRuleFile, result );
		addIgnoreRuleFiles( userIgnoreRuleFile, result );

		if ( ignoreFilesBasePath != null ) {
			final Path suiteIgnoreRuleFile = jsIgnoreLocator.getSuiteIgnoreFile( ignoreFilesBasePath );
			addIgnoreRuleFiles( suiteIgnoreRuleFile, result );
		}
	}

	private Stream<String> getIgnoreFileLines( final Optional<Path> ignoreFile ) throws IOException {
		if ( ignoreFile.isPresent() ) {
			return getIgnoreFileLines( ignoreFile.get() );
		}
		log.info( "Ignoring missing ignore file." );
		return Stream.empty();
	}

	private Stream<String> getIgnoreFileLines( final Path ignoreFile ) throws IOException {
		if ( ignoreFile.toFile().exists() ) {
			log.info( "Reading ignore file from '{}'.", ignoreFile );
			return Files.lines( ignoreFile );
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
