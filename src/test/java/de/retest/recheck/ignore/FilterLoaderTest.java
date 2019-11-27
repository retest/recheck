package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

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
}
