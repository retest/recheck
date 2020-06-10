package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.SearchFilterFiles.FilterResource;

class SearchFilterFilesIT {

	@Nested
	class FilterResourceIT {

		@Test
		void loader_should_properly_load_resource() {
			final FilterResource cut = FilterResource.prefix( "web", "content.filter" );

			final Pair<String, FilterLoader> pair = cut.loader();
			final FilterLoader loader = pair.getRight();

			assertThatCode( loader::load ).doesNotThrowAnyException();
		}
	}
}
