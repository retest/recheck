package de.retest.recheck.review.workers;

import static java.nio.file.Files.write;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import de.retest.recheck.ignore.CacheFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.ignore.RecheckIgnoreLocator;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.ignore.io.Loaders;

public class SaveFilterWorker {

	private final GlobalIgnoreApplier applier;
	private final RecheckIgnoreLocator locator;

	public SaveFilterWorker( final GlobalIgnoreApplier applier ) {
		this( applier, new RecheckIgnoreLocator() );
	}

	protected SaveFilterWorker( final GlobalIgnoreApplier applier, final RecheckIgnoreLocator locator ) {
		this.applier = applier;
		this.locator = locator;
	}

	public void save() throws IOException {
		final Optional<Path> ignorePath = locator.getProjectIgnoreFile();
		final PersistableGlobalIgnoreApplier persist = applier.persist();

		// Filter JSFilter because that would create unnecessary file content.
		final Stream<Filter> filters = persist.getIgnores().stream() //
				.map( this::extractCachedFilter ) //
				.filter( filter -> !(filter instanceof JSFilterImpl) );
		final Stream<String> save = Loaders.filter().save( filters );

		write( ignorePath.orElse( locator.getUserIgnoreFile() ), (Iterable<String>) save::iterator );
	}

	private Filter extractCachedFilter( final Filter filter ) {
		return filter instanceof CacheFilter ? ((CacheFilter) filter).getBase() : filter;
	}
}
