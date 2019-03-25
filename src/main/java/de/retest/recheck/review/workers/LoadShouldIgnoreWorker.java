package de.retest.recheck.review.workers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ignore.JSShouldIgnoreImpl;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.io.Loaders;

public class LoadShouldIgnoreWorker {

	private final Counter counter;

	public LoadShouldIgnoreWorker( final Counter counter ) {
		this.counter = counter;
	}

	public GlobalIgnoreApplier load() throws IOException {
		final Optional<Path> ignoreFile = RecheckIgnoreUtil.getIgnoreFile();
		final Stream<String> ignoreFileLines = Files
				.lines( ignoreFile.orElseThrow( () -> new IllegalArgumentException( "No recheck.ignore found." ) ) );
		final PersistableGlobalIgnoreApplier ignoreApplier = Loaders.load( ignoreFileLines ) //
				.filter( ShouldIgnore.class::isInstance ) //
				.map( ShouldIgnore.class::cast ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		final GlobalIgnoreApplier result = GlobalIgnoreApplier.create( counter, ignoreApplier );

		RecheckIgnoreUtil.getIgnoreRuleFile().ifPresent( file -> result.add( new JSShouldIgnoreImpl( file ) ) );

		return result;
	}

	public Counter getCounter() {
		return counter;
	}
}
