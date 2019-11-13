package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

class ContentFilterIT {

	@Test
	void should_filter_img_src_and_alt_diffs() throws Exception {
		final Filter filter = FilterLoader.load( Paths.get( "src/main/resources/filter/web/content.filter" ) ).load();

		final Element element = Element.create( "retestId", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "/html[1]/img[1]" ), "img" ),
				new MutableAttributes().immutable() );
		final AttributeDifference srcDiff =
				new AttributeDifference( "src", "https://old/img.jpg", "https://new/img.jpg" );
		final AttributeDifference altDiff = new AttributeDifference( "alt", "A house.", "A dog." );
		final AttributeDifference clipDiff = new AttributeDifference( "clip", "bla", "blub" );

		assertThat( filter.matches( element ) ).isFalse();
		assertThat( filter.matches( element, srcDiff ) ).isTrue();
		assertThat( filter.matches( element, altDiff ) ).isTrue();
		assertThat( filter.matches( element, clipDiff ) ).isFalse();
	}

	@Test
	void should_filter_text_diffs() throws Exception {
		final Filter filter = FilterLoader.load( Paths.get( "src/main/resources/filter/web/content.filter" ) ).load();

		final Element element = Element.create( "retestId", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "/html[1]/p[1]" ), "p" ),
				new MutableAttributes().immutable() );
		final AttributeDifference textDiff = new AttributeDifference( "text", "An ancient text.", "A hip new text." );
		final AttributeDifference otherDiff = new AttributeDifference( "alt", "A house.", "A dog." );

		assertThat( filter.matches( element ) ).isFalse();
		assertThat( filter.matches( element, textDiff ) ).isTrue();
		assertThat( filter.matches( element, otherDiff ) ).isFalse();
	}
}
