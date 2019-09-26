package de.retest.recheck.ui.descriptors.idproviders;

import static de.retest.recheck.ui.Path.fromString;
import static de.retest.recheck.ui.descriptors.IdentifyingAttributes.create;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ElementCountingRetestIdProviderTest {

	@Test
	void test() {
		final ElementCountingRetestIdProvider cut = new ElementCountingRetestIdProvider();
		assertThat( cut.getRetestId( create( fromString( "/html[1]/div[1]" ), "div" ) ) ).isEqualTo( "div-1" );
		assertThat( cut.getRetestId( create( fromString( "/html[1]/div[1]/div[1]" ), "div" ) ) ).isEqualTo( "div-2" );
		assertThat( cut.getRetestId( create( fromString( "/html[1]/div[4]" ), "div" ) ) ).isEqualTo( "div-3" );
		assertThat( cut.getRetestId( create( fromString( "/html[1]/a[1]" ), "a" ) ) ).isEqualTo( "a-1" );
	}

}
