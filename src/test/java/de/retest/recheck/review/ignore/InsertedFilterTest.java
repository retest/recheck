package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.Filter.ChangeType;
import de.retest.recheck.review.ignore.InsertedFilter.InsertedFilterLoader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.ui.descriptors.Element;

class InsertedFilterTest {

	@Test
	void should_filter_inserted_elements() {
		InsertedFilter cut = new InsertedFilter();
		Element element = Mockito.mock( Element.class );
		assertThat( cut.matches( element, ChangeType.INSERTED ) ).isTrue();
	}

	@Test
	void should_not_filter_noninserted_elements() {
		InsertedFilter cut = new InsertedFilter();
		Element element = Mockito.mock( Element.class );
		assertThat( cut.matches( element ) ).isFalse();
		assertThat( cut.matches( element, ChangeType.DELETED ) ).isFalse();
		assertThat( cut.matches( element, ChangeType.CHANGED ) ).isFalse();
	}

	@Test
	void should_load_filter_correctly() {
		InsertedFilterLoader loader = new InsertedFilterLoader();
		assertThat( loader.load( "change=inserted" ).get() ).isInstanceOf( InsertedFilter.class );

		String line = "matcher: id=myId, change=inserted";
		Filter filter = Loaders.filter().load( line ).get();
		assertThat( filter ).isInstanceOf( AllMatchFilter.class );
		assertThat( ((AllMatchFilter) filter).getFilters() ).hasToString( "[" + line + "]" );
	}

	@Test
	void should_work_with_other_filters() {
		Filter filter = Loaders.filter().load( "matcher: retestid=myId, change=inserted" ).get();
		assertThat( filter ).isInstanceOf( AllMatchFilter.class );

		Element matching = Mockito.mock( Element.class );
		when( matching.getRetestId() ).thenReturn( "myId" );

		assertThat( filter.matches( matching, ChangeType.INSERTED ) ).isTrue();

		Element nonMatching = Mockito.mock( Element.class );
		when( nonMatching.getRetestId() ).thenReturn( "otherId" );

		assertThat( filter.matches( nonMatching, ChangeType.INSERTED ) ).isFalse();
	}
}
