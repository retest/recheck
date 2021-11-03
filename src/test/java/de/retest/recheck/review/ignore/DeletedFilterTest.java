package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.Filter.ChangeType;
import de.retest.recheck.review.ignore.DeletedFilter.DeletedFilterLoader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.ui.descriptors.Element;

class DeletedFilterTest {

	@Test
	void should_filter_deleted_elements() {
		final DeletedFilter cut = new DeletedFilter();
		final Element element = mock( Element.class );
		assertThat( cut.matches( element, ChangeType.DELETED ) ).isTrue();
	}

	@Test
	void should_not_filter_nondeleted_elements() {
		final DeletedFilter cut = new DeletedFilter();
		final Element element = mock( Element.class );
		assertThat( cut.matches( element ) ).isFalse();
		assertThat( cut.matches( element, ChangeType.INSERTED ) ).isFalse();
		assertThat( cut.matches( element, ChangeType.CHANGED ) ).isFalse();
	}

	@Test
	void should_load_filter_correctly() {
		final DeletedFilterLoader loader = new DeletedFilterLoader();
		assertThat( loader.load( "change=deleted" ).get() ).isInstanceOf( DeletedFilter.class );

		final String line = "matcher: id=myId, change=deleted";
		final Filter filter = Loaders.filter().load( line ).get();
		assertThat( filter ).isInstanceOf( AllMatchFilter.class );
		assertThat( ((AllMatchFilter) filter).getFilters() ).hasToString( "[" + line + "]" );
	}

	@Test
	void should_work_with_other_filters() {
		final Filter filter = Loaders.filter().load( "matcher: retestid=myId, change=deleted" ).get();
		assertThat( filter ).isInstanceOf( AllMatchFilter.class );

		final Element matching = mock( Element.class );
		when( matching.getRetestId() ).thenReturn( "myId" );

		assertThat( filter.matches( matching, ChangeType.DELETED ) ).isTrue();

		final Element nonMatching = mock( Element.class );
		when( nonMatching.getRetestId() ).thenReturn( "otherId" );

		assertThat( filter.matches( nonMatching, ChangeType.DELETED ) ).isFalse();
	}
}
