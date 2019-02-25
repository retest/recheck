package de.retest.recheck.review.workers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

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
		final Optional<Path> path = RecheckIgnoreUtil.getIgnoreFile();
		final PersistableGlobalIgnoreApplier ignoreApplier = Loaders
				.load( Files.lines(
						path.orElseThrow( () -> new IllegalArgumentException( "No reliable argument found." ) ) ) ) //
				.filter( ShouldIgnore.class::isInstance ) //
				.map( ShouldIgnore.class::cast ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		return GlobalIgnoreApplier.create( counter, ignoreApplier );
	}

	public Counter getCounter() {
		return counter;
	}
}
