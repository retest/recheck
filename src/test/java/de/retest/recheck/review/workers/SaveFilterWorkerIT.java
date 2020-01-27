package de.retest.recheck.review.workers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import de.retest.recheck.ignore.CacheFilter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.ignore.PersistentFilter;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;

class SaveFilterWorkerIT {

	@TempDir
	Path base;

	static GlobalIgnoreApplier createApplier( final List<PersistentFilter> filter ) {
		final PersistableGlobalIgnoreApplier persist = mock( PersistableGlobalIgnoreApplier.class );
		final GlobalIgnoreApplier applier = mock( GlobalIgnoreApplier.class );
		when( applier.persist() ).thenReturn( persist );
		when( persist.getIgnores() ).thenReturn( filter );
		return applier;
	}

	static GlobalIgnoreApplier createApplier( final Path path ) {
		return createApplier( singletonList( new PersistentFilter( path, new FilterPreserveLine( "# Comment" ) ) ) );
	}

	@Test
	void not_existent_parent_folder_should_be_created_upon_save() throws IOException {
		final Path nonExistentProjectFolder = base.resolve( ".retest/recheck.ignore" );
		final SaveFilterWorker cut = new SaveFilterWorker( createApplier( nonExistentProjectFolder ) );

		cut.save();

		assertThat( base.resolve( ".retest" ) ).exists();
	}

	@Test
	void persistence_should_use_given_path() throws IOException {
		final Path irregularFile = base.resolve( "somethingelse" );
		final SaveFilterWorker cut = new SaveFilterWorker( createApplier( irregularFile ) );

		cut.save();

		assertThat( irregularFile ).exists();
		assertThat( irregularFile.toFile() ).hasContent( "# Comment" );
	}

	@Test
	void multiple_different_files_should_be_resolved_correctly() throws IOException {
		final Path file1 = base.resolve( "file1" );
		final Path file2 = base.resolve( "file2" );
		final Path file3 = base.resolve( "file3" );
		final Path jsFile = base.resolve( "jsFile" );

		final List<PersistentFilter> filter = new ArrayList<>();
		filter.add( new PersistentFilter( file1, new FilterPreserveLine( "# A" ) ) );
		filter.add( new PersistentFilter( file2, new FilterPreserveLine( "# B" ) ) );
		filter.add( new PersistentFilter( file1, new FilterPreserveLine( "# C" ) ) );
		filter.add( new PersistentFilter( file1, new FilterPreserveLine( "# D" ) ) );
		filter.add( new PersistentFilter( file3, new FilterPreserveLine( "# E" ) ) );
		filter.add( new PersistentFilter( file1, new FilterPreserveLine( "# C" ) ) );
		filter.add( new PersistentFilter( jsFile, new CacheFilter( Mockito.mock( JSFilterImpl.class ) ) ) );

		final SaveFilterWorker cut = new SaveFilterWorker( createApplier( filter ) );

		cut.save();

		assertThat( file1 ).hasContent( "# A\n# C\n# D\n# C" );
		assertThat( file2 ).hasContent( "# B" );
		assertThat( file3 ).hasContent( "# E" );

		assertThat( jsFile ).doesNotExist();
	}
}
