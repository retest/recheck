package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.io.Loaders;

public class LoadFilterWorker {

	private final Counter counter;
	private final File ignoreFilesBasePath;

	public LoadFilterWorker( final Counter counter ) {
		this( counter, null );
	}

	public LoadFilterWorker( final Counter counter, final File ignoreFilesBasePath ) {
		this.counter = counter;
		this.ignoreFilesBasePath = ignoreFilesBasePath;
	}

	public GlobalIgnoreApplier load() throws IOException {
		final Optional<Path> ignoreFile = getIgnoreFile();
		final Stream<String> ignoreFileLines = Files.lines(
				ignoreFile.orElseThrow( () -> new IllegalArgumentException( "No '" + RECHECK_IGNORE + "' found." ) ) );
		final PersistableGlobalIgnoreApplier ignoreApplier = Loaders.load( ignoreFileLines ) //
				.filter( Filter.class::isInstance ) //
				.map( Filter.class::cast ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		final Optional<Path> ignoreRuleFile = getIgnoreRuleFile();
		ignoreRuleFile.ifPresent( file -> result.add( new JSFilterImpl( file ) ) );

		return result;
	}

	private Optional<Path> getIgnoreFile() {
		return ignoreFilesBasePath != null ? resolveAndCheckFile( ignoreFilesBasePath, RECHECK_IGNORE )
				: RecheckIgnoreUtil.getIgnoreFile();
	}

	private Optional<Path> getIgnoreRuleFile() {
		return ignoreFilesBasePath != null ? resolveAndCheckFile( ignoreFilesBasePath, RECHECK_IGNORE_JSRULES )
				: RecheckIgnoreUtil.getIgnoreRuleFile();
	}

	private static Optional<Path> resolveAndCheckFile( final File basePath, final String filename ) {
		final File resolved = new File( basePath, filename );
		return resolved.exists() ? Optional.of( resolved.toPath() ) : Optional.empty();
	}

	public Counter getCounter() {
		return counter;
	}
}
