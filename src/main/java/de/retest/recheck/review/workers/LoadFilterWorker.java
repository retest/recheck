package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
		final Optional<Path> projectIgnoreFile = RecheckIgnoreUtil.getProjectIgnoreFile( RECHECK_IGNORE );
		final Optional<Path> userIgnoreFile = RecheckIgnoreUtil.getUserIgnoreFile( RECHECK_IGNORE );
		final Stream<String> concatIgnoreFileLines =
				Stream.concat( getIgnoreFileLines( projectIgnoreFile ), getIgnoreFileLines( userIgnoreFile ) );
		Stream<String> allIgnoreFileLines = concatIgnoreFileLines;

		if ( ignoreFilesBasePath != null ) {
			final Optional<Path> suiteIgnoreFile =
					RecheckIgnoreUtil.getSuiteIgnoreFile( RECHECK_IGNORE, ignoreFilesBasePath );
			allIgnoreFileLines = Stream.concat( concatIgnoreFileLines, getIgnoreFileLines( suiteIgnoreFile ) );
		}

		final PersistableGlobalIgnoreApplier ignoreApplier = Loaders.filter().load( allIgnoreFileLines ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		final Optional<Path> projectIgnoreRuleFile = RecheckIgnoreUtil.getProjectIgnoreFile( RECHECK_IGNORE_JSRULES );
		final Optional<Path> userIgnoreRuleFile = RecheckIgnoreUtil.getUserIgnoreFile( RECHECK_IGNORE_JSRULES );
		projectIgnoreRuleFile
				.ifPresent( file -> result.addWithoutCounting( new CacheFilter( new JSFilterImpl( file ) ) ) );
		userIgnoreRuleFile
				.ifPresent( file -> result.addWithoutCounting( new CacheFilter( new JSFilterImpl( file ) ) ) );

		if ( ignoreFilesBasePath != null ) {
			final Optional<Path> suiteIgnoreRuleFile =
					RecheckIgnoreUtil.getSuiteIgnoreFile( RECHECK_IGNORE_JSRULES, ignoreFilesBasePath );
			suiteIgnoreRuleFile
					.ifPresent( file -> result.addWithoutCounting( new CacheFilter( new JSFilterImpl( file ) ) ) );
		}

		return result;
	}

	private Stream<String> getIgnoreFileLines( final Optional<Path> ignoreFile ) throws IOException {
		if ( ignoreFile.isPresent() ) {
			log.info( "Reading ignore file from {}.", ignoreFile.get() );
			return Files.lines(
					ignoreFile.orElseThrow( () -> new NoSuchFileException( "No '" + RECHECK_IGNORE + "' found." ) ) );
		}
		log.info( "Ignoring missing ignore file." );
		return Stream.empty();
	}

	public Counter getCounter() {
		return counter;
	}
}
