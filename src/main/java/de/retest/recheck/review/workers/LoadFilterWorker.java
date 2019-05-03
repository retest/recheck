package de.retest.recheck.review.workers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.io.Loaders;

public class LoadFilterWorker {

	private final Counter counter;
	private final Optional<Path> ignoreFile;
	private final Optional<Path> ignoreRuleFile;

	public LoadFilterWorker( final Counter counter ) {
		this.counter = counter;

		ignoreFile = RecheckIgnoreUtil.getIgnoreFile();
		ignoreRuleFile = RecheckIgnoreUtil.getIgnoreRuleFile();
	}

	public LoadFilterWorker( final Counter counter, final File ignoreFilesBasePath ) {
		this.counter = counter;

		ignoreFile = resolveAndCheckFile( ignoreFilesBasePath, ProjectConfiguration.RECHECK_IGNORE );
		ignoreRuleFile = resolveAndCheckFile( ignoreFilesBasePath, ProjectConfiguration.RECHECK_IGNORE_JSRULES );
	}

	private static Optional<Path> resolveAndCheckFile( final File basePath, final String filename ) {
		final File resolved = new File( basePath, filename );
		return resolved.exists() ? Optional.of( resolved.toPath() ) : Optional.empty();
	}

	public GlobalIgnoreApplier load() throws IOException {

		final Stream<String> ignoreFileLines = Files
				.lines( ignoreFile.orElseThrow( () -> new IllegalArgumentException( "No recheck.ignore found." ) ) );
		final PersistableGlobalIgnoreApplier ignoreApplier = Loaders.load( ignoreFileLines ) //
				.filter( Filter.class::isInstance ) //
				.map( Filter.class::cast ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		ignoreRuleFile.ifPresent( file -> result.add( new JSFilterImpl( file ) ) );

		return result;
	}

	public Counter getCounter() {
		return counter;
	}
}
