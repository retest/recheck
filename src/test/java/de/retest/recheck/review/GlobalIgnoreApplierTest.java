package de.retest.recheck.review;

import static de.retest.recheck.review.counter.NopCounter.getInstance;
import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

class GlobalIgnoreApplierTest {

	private static class some {
	}

	@Test
	void ignoreElement_should_add_filter() {
		final GlobalIgnoreApplier cut = GlobalIgnoreApplier.create( getInstance() );

		final Element element = Element.create( "myRetestId", mock( Element.class ),
				IdentifyingAttributes.create( fromString( "/other[1]/some[1]" ), some.class ),
				new MutableAttributes().immutable() );

		cut.ignoreElement( element );

		assertThat( cut.persist().getIgnores().toString() ).isEqualTo( "[matcher: retestid=myRetestId]" );
	}

	@Test
	void ignoreAttribute_should_add_filter() {
		final GlobalIgnoreApplier cut = GlobalIgnoreApplier.create( getInstance() );

		final Element element = Element.create( "myRetestId", mock( Element.class ),
				IdentifyingAttributes.create( fromString( "/other[1]/some[1]" ), some.class ),
				new MutableAttributes().immutable() );

		cut.ignoreAttribute( element, new AttributeDifference( "myAttribute", "expected", "actual" ) );

		assertThat( cut.persist().getIgnores().toString() )
				.isEqualTo( "[matcher: retestid=myRetestId, attribute: myAttribute]" );
	}
}
