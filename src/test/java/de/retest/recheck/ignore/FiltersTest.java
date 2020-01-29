package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

class FiltersTest {

	@Test
	void load_from_path_should_work_properly() throws Exception {
		check( Filters.load( Paths.get( getClass().getResource( "file.filter" ).toURI() ) ) );
	}

	@Test
	void parse_with_single_line_should_work() {
		check( Filters.parse( "matcher: id=foo, attribute=font" ) );
	}

	@Test
	void parse_with_stream_should_work() {
		check( Filters.parse( Stream.of( "matcher: id=foo, attribute=font" ) ) );
	}

	private void check( final Filter filter ) {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "id" ) ).thenReturn( "foo" );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "font" );

		assertThat( filter.matches( element ) ).isFalse();
		assertThat( filter.matches( element, difference ) ).isTrue();
	}

}
