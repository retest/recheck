package de.retest.recheck.review.workers;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ignore.CacheFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.PersistentFilter;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.ignore.io.Loaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveFilterWorker {

	private final GlobalIgnoreApplier applier;

	public SaveFilterWorker( final GlobalIgnoreApplier applier ) {
		this.applier = applier;
	}

	public void save() throws IOException {
		final PersistableGlobalIgnoreApplier persist = applier.persist();

		// Filter JSFilter because that would create unnecessary file content.
		final Stream<PersistentFilter> filters = persist.getIgnores().stream() //
				.map( this::extractCachedFilter ) //
				.filter( filter -> (filter instanceof PersistentFilter) ) //
				.map( f -> (PersistentFilter) f );

		final Map<Path, List<PersistentFilter>> mapped =
				filters.collect( Collectors.groupingBy( PersistentFilter::getPath ) );

		for ( final Map.Entry<Path, List<PersistentFilter>> entry : mapped.entrySet() ) {
			final List<String> save = entry.getValue().stream() //
					.map( f -> Loaders.filter().save( f.getFilter() ) ) //
					.collect( Collectors.toList() );
			final Path target = entry.getKey();
			log.info( "Writing filters to file {}.", target );
			createDirectories( target.getParent() );
			write( target, save );
		}
	}

	private Filter extractCachedFilter( final Filter filter ) {
		return filter instanceof CacheFilter ? ((CacheFilter) filter).getBase() : filter;
	}
}
