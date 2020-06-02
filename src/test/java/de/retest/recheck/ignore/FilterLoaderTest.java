package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;

class FilterLoaderTest {

	@Test
	void loading_filterjs_should_load_JavaScript() throws Exception {
		final FilterLoader loader =
				FilterLoader.load( Paths.get( getClass().getResource( "file.filter.js" ).toURI() ) );
		final Filter filter = loader.load();
		assertThat( filter ).isInstanceOf( JSFilterImpl.class );
		final Element element = null;
		final String attributeKey = null;
		assertThat( filter.matches( element, attributeKey ) ).isTrue();
	}

	@Test
	void loadResource_should_properly_throw_descriptive_exception_if_resource_not_present() {
		final FilterLoader cut = FilterLoader.loadResource( "/not-present-file.file" );

		assertThatThrownBy( cut::load ).isInstanceOf( NoSuchFileException.class );
	}
}
