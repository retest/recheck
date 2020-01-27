package de.retest.recheck.review;

import static de.retest.recheck.ignore.PersistentFilter.unwrap;
import static de.retest.recheck.review.counter.NopCounter.getInstance;
import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.ignore.RecheckIgnoreLocator;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

class GlobalIgnoreApplierTest {

	private static class some {}

	@TempDir
	Path base;

	@Test
	void ignoreElement_should_add_filter() {
		final GlobalIgnoreApplier cut = GlobalIgnoreApplier.create( getInstance() );

		cut.ignoreElement( createFake() );

		assertThat( unwrap( cut.persist().getIgnores() ).toString() ).isEqualTo( "[matcher: retestid=myRetestId]" );
	}

	Element createFake() {
		return Element.create( "myRetestId", mock( Element.class ),
				IdentifyingAttributes.create( fromString( "/other[1]/some[1]" ), some.class ),
				new MutableAttributes().immutable() );
	}

	@Test
	void ignoreAttribute_should_add_filter() {
		final GlobalIgnoreApplier cut = GlobalIgnoreApplier.create( getInstance() );

		cut.ignoreAttribute( createFake(), new AttributeDifference( "myAttribute", "expected", "actual" ) );

		assertThat( unwrap( cut.persist().getIgnores() ).toString() )
				.isEqualTo( "[matcher: retestid=myRetestId, attribute=myAttribute]" );
	}

	@Test
	void project_should_be_preferred_over_userhome() {
		final Path projectIgnore = base.resolve( "project/recheck.ignore" );
		final RecheckIgnoreLocator locateNonExistentUserHome = new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.of( projectIgnore );
			}

			@Override
			public Path getUserIgnoreFile() {
				return base.resolve( "userhome/recheck.ignore" );
			}
		};
		final GlobalIgnoreApplier cut = GlobalIgnoreApplier.create( getInstance(), locateNonExistentUserHome );

		cut.ignoreElement( createFake() );

		assertThat( cut.persist().getIgnores().get( 0 ).getPath() ).isEqualTo( projectIgnore );
	}

	@Test
	void if_not_in_project_context_should_use_user_home() {
		final Path userhomeIgnore = base.resolve( "userhome/recheck.ignore" );
		final RecheckIgnoreLocator locateUserHome = new RecheckIgnoreLocator() {
			@Override
			public Optional<Path> getProjectIgnoreFile() {
				return Optional.empty();
			}

			@Override
			public Path getUserIgnoreFile() {
				return userhomeIgnore;
			}
		};

		final GlobalIgnoreApplier cut = GlobalIgnoreApplier.create( getInstance(), locateUserHome );

		cut.ignoreElement( createFake() );

		assertThat( cut.persist().getIgnores().get( 0 ).getPath() ).isEqualTo( userhomeIgnore );
	}
}
