package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ignore.CacheFilter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.io.Loaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadFilterWorker {

	private final Counter counter;
	private final Path ignoreFilesBasePath;

	public LoadFilterWorker( final Counter counter ) {
		this( counter, null );
	}

	public LoadFilterWorker( final Counter counter, final Path ignoreFilesBasePath ) {
		this.counter = counter;
		this.ignoreFilesBasePath = ignoreFilesBasePath;
	}

	public GlobalIgnoreApplier load() throws IOException {
		final Stream<String> allIgnoreFileLines = readIgnoreFileLines();

		final PersistableGlobalIgnoreApplier ignoreApplier = Loaders.filter().load( allIgnoreFileLines ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		readIgnoreRuleFileLines( result );
		return result;
	}

	private Stream<String> readIgnoreFileLines() throws IOException {
		final Optional<Path> projectIgnoreFile = RecheckIgnoreUtil.getProjectIgnoreFile( RECHECK_IGNORE );
		final Path userIgnoreFile = RecheckIgnoreUtil.getUserIgnoreFile( RECHECK_IGNORE );
		final Stream<String> concatIgnoreFileLines =
				Stream.concat( getIgnoreFileLines( projectIgnoreFile ), getIgnoreFileLines( userIgnoreFile ) );
		Stream<String> allIgnoreFileLines = concatIgnoreFileLines;

		if ( ignoreFilesBasePath != null ) {
			final Path suiteIgnoreFile = RecheckIgnoreUtil.getSuiteIgnoreFile( RECHECK_IGNORE, ignoreFilesBasePath );
			allIgnoreFileLines = Stream.concat( concatIgnoreFileLines, getIgnoreFileLines( suiteIgnoreFile ) );
		}
		return allIgnoreFileLines;
	}

	private void readIgnoreRuleFileLines( final GlobalIgnoreApplier result ) throws IOException {
		final Optional<Path> projectIgnoreRuleFile = RecheckIgnoreUtil.getProjectIgnoreFile( RECHECK_IGNORE_JSRULES );
		final Path userIgnoreRuleFile = RecheckIgnoreUtil.getUserIgnoreFile( RECHECK_IGNORE_JSRULES );
		addIgnoreRuleFiles( projectIgnoreRuleFile, result );
		addIgnoreRuleFiles( userIgnoreRuleFile, result );

		if ( ignoreFilesBasePath != null ) {
			final Path suiteIgnoreRuleFile =
					RecheckIgnoreUtil.getSuiteIgnoreFile( RECHECK_IGNORE_JSRULES, ignoreFilesBasePath );
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

	private void addIgnoreRuleFiles( final Optional<Path> ignoreRuleFile, final GlobalIgnoreApplier result )
			throws IOException {
		if ( ignoreRuleFile.isPresent() ) {
			addIgnoreRuleFiles( ignoreRuleFile.get(), result );
		}
	}

	private void addIgnoreRuleFiles( final Path ignoreRuleFile, final GlobalIgnoreApplier result ) throws IOException {
		if ( ignoreRuleFile.toFile().exists() ) {
			result.addWithoutCounting( new CacheFilter( new JSFilterImpl( ignoreRuleFile ) ) );
		}
	}

	public Counter getCounter() {
		return counter;
	}
}
