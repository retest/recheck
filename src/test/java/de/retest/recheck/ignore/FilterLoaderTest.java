package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class FilterLoaderTest {

	@Test
	void loading_filterjs_should_load_JavaScript() throws Exception {
		final FilterLoader loader =
				FilterLoader.load( Paths.get( getClass().getResource( "file.filter.js" ).toURI() ) );
		final Filter filter = loader.load();
		assertThat( filter ).isInstanceOf( JSFilterImpl.class );
		assertThat( filter.matches( null, null ) ).isTrue();
	}
}
