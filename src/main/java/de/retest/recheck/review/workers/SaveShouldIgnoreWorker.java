package de.retest.recheck.review.workers;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectConfigurationUtil;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.ignore.io.Loaders;

public class SaveShouldIgnoreWorker {

	private static final Logger logger = LoggerFactory.getLogger( SaveShouldIgnoreWorker.class );

	private final GlobalIgnoreApplier applier;

	public SaveShouldIgnoreWorker( final GlobalIgnoreApplier applier ) {
		this.applier = applier;
	}

	public void save() throws Exception {
		final Path path = ProjectConfigurationUtil.findProjectConfigurationFolder().resolve( "recheck.ignore" );
		final PersistableGlobalIgnoreApplier persist = applier.persist();

		final Stream<String> save = Loaders.save( persist.getIgnores().stream() );

		try ( final PrintStream writer = new PrintStream( Files.newOutputStream( path ) ) ) {
			save.forEach( writer::println );
		}
	}
}
