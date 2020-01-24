package de.retest.recheck.review.workers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.ignore.PersistentFilter;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;

class SaveFilterWorkerIT {

	@TempDir
	Path base;

	static GlobalIgnoreApplier createApplier( final Path path ) {
		final PersistableGlobalIgnoreApplier persist = mock( PersistableGlobalIgnoreApplier.class );
		final GlobalIgnoreApplier applier = mock( GlobalIgnoreApplier.class );
		when( applier.persist() ).thenReturn( persist );
		when( persist.getIgnores() )
				.thenReturn( singletonList( new PersistentFilter( path, new FilterPreserveLine( "# Comment" ) ) ) );
		return applier;
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
}
