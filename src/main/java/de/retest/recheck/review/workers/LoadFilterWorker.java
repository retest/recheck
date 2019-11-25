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
		final Optional<Path> ignoreFile = getIgnoreFile();
		ignoreFile.ifPresent( path -> log.info( "Reading ignore file from {}.", path ) );
		final Stream<String> ignoreFileLines = Files.lines(
				ignoreFile.orElseThrow( () -> new NoSuchFileException( "No '" + RECHECK_IGNORE + "' found." ) ) );
		final PersistableGlobalIgnoreApplier ignoreApplier = Loaders.filter().load( ignoreFileLines ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		final Optional<Path> ignoreRuleFile = getIgnoreRuleFile();
		ignoreRuleFile.ifPresent( file -> result.addWithoutCounting( new CacheFilter( new JSFilterImpl( file ) ) ) );

		return result;
	}

	private Optional<Path> getIgnoreFile() {
		return ignoreFilesBasePath != null ? resolveAndCheckFile( RECHECK_IGNORE ) : RecheckIgnoreUtil.getIgnoreFile();
	}

	private Optional<Path> getIgnoreRuleFile() {
		return ignoreFilesBasePath != null ? resolveAndCheckFile( RECHECK_IGNORE_JSRULES )
				: RecheckIgnoreUtil.getIgnoreRuleFile();
	}

	private Optional<Path> resolveAndCheckFile( final String ignoreFilename ) {
		final Path resolved = ignoreFilesBasePath.resolve( ignoreFilename );
		return Files.exists( resolved ) ? Optional.of( resolved ) : Optional.empty();
	}

	public Counter getCounter() {
		return counter;
	}
}
