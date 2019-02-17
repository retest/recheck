package de.retest.recheck.review.workers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectConfigurationUtil;
import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.io.Loaders;

public class LoadShouldIgnoreWorker {

	private static final Logger logger = LoggerFactory.getLogger( LoadShouldIgnoreWorker.class );

	private final Counter counter;
	private final Consumer<GlobalIgnoreApplier> applier;

	public LoadShouldIgnoreWorker( final Counter counter, final Consumer<GlobalIgnoreApplier> applier ) {
		this.counter = counter;
		this.applier = applier;
	}

	public GlobalIgnoreApplier load() throws Exception {
		final Path path = ProjectConfigurationUtil.findProjectConfigurationFolder().resolve( "recheck.ignore" );
		final PersistableGlobalIgnoreApplier applier = Loaders.load( Files.lines( path ) ) //
				.filter( ShouldIgnore.class::isInstance ) //
				.map( ShouldIgnore.class::cast ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), PersistableGlobalIgnoreApplier::new ) );
		return GlobalIgnoreApplier.create( counter, applier );
	}
}
