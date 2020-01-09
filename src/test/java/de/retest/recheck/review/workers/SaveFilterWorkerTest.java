package de.retest.recheck.review.workers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import de.retest.recheck.ignore.RecheckIgnoreLocator;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.GlobalIgnoreApplier.PersistableGlobalIgnoreApplier;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;

class SaveFilterWorkerTest {

	@Rule
	final TemporaryFolder folder = new TemporaryFolder();

	static GlobalIgnoreApplier applier;

	@BeforeAll
	static void setup() {
		final PersistableGlobalIgnoreApplier persist = mock( PersistableGlobalIgnoreApplier.class );
		applier = mock( GlobalIgnoreApplier.class );
		when( applier.persist() ).thenReturn( persist );
		when( persist.getIgnores() ).thenReturn( singletonList( new FilterPreserveLine( "# Comment" ) ) );
	}

	@Test
	void not_existend_project_folder_should_be_created_upon_save() throws IOException {
		folder.create();
		final Path base = folder.getRoot().toPath();

		final SaveFilterWorker cut = new SaveFilterWorker( applier, new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.of( base.resolve( ".retest/recheck.ignore" ) );
			}
		} );

		cut.save();

		assertThat( base.resolve( ".retest" ).toFile() ).exists();
	}

	@Test
	void use_user_home_if_not_in_project_context() throws IOException {
		folder.create();
		final Path base = folder.getRoot().toPath();

		final SaveFilterWorker cut = new SaveFilterWorker( applier, new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.empty();
			}

			@Override
			public Path getUserIgnoreFile() {
				return base.resolve( "recheck.ignore" );
			}
		} );

		cut.save();

		assertThat( base.resolve( "recheck.ignore" ).toFile() ).hasContent( "# Comment" );
	}

	@Test
	void not_existend_user_home_should_be_created_upon_save() throws IOException {
		folder.create();
		final Path base = folder.getRoot().toPath();

		final SaveFilterWorker cut = new SaveFilterWorker( applier, new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.empty();
			}

			@Override
			public Path getUserIgnoreFile() {
				return base.resolve( ".retest/recheck.ignore" );
			}
		} );

		cut.save();

		assertThat( base.resolve( ".retest" ).toFile() ).exists();
	}
}
