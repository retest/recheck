package de.retest.recheck.review.workers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.ignore.RecheckIgnoreLocator;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;

class SaveFilterWorkerIT {

	@TempDir
	Path base;

	static GlobalIgnoreApplier applier;

	@BeforeAll
	static void setup() {
		final PersistableGlobalIgnoreApplier persist = mock( PersistableGlobalIgnoreApplier.class );
		applier = mock( GlobalIgnoreApplier.class );
		when( applier.persist() ).thenReturn( persist );
		when( persist.getIgnores() ).thenReturn( singletonList( new FilterPreserveLine( "# Comment" ) ) );
	}

	@Test
	void not_existent_project_folder_should_be_created_upon_save() throws IOException {
		final RecheckIgnoreLocator locateNonExistentProjectFolder = new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.of( base.resolve( ".retest/recheck.ignore" ) );
			}
		};

		final SaveFilterWorker cut = new SaveFilterWorker( applier, locateNonExistentProjectFolder );

		cut.save();

		assertThat( base.resolve( ".retest" ) ).exists();
	}

	@Test
	void if_not_in_project_context_should_use_user_home() throws IOException {
		final RecheckIgnoreLocator locateUserHome = new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.empty();
			}

			@Override
			public Path getUserIgnoreFile() {
				return base.resolve( "recheck.ignore" );
			}
		};

		final SaveFilterWorker cut = new SaveFilterWorker( applier, locateUserHome );

		cut.save();

		assertThat( base.resolve( "recheck.ignore" ) ).hasContent( "# Comment" );
	}

	@Test
	void not_existent_user_home_should_be_created_upon_save() throws IOException {
		final RecheckIgnoreLocator locateNonExistentUserHome = new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.empty();
			}

			@Override
			public Path getUserIgnoreFile() {
				return base.resolve( ".retest/recheck.ignore" );
			}
		};
		final SaveFilterWorker cut = new SaveFilterWorker( applier, locateNonExistentUserHome );

		cut.save();

		assertThat( base.resolve( ".retest" ) ).exists();
	}
}
