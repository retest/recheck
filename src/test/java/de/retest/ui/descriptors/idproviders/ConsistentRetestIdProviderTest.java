package de.retest.ui.descriptors.idproviders;

import static de.retest.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.idproviders.ConsistentRetestIdProvider;
import de.retest.ui.descriptors.idproviders.RetestIdProvider;

public class ConsistentRetestIdProviderTest {

	@Test
	public void random_id_should_be_consistent() {
		final RetestIdProvider cut = new ConsistentRetestIdProvider( new RetestIdProvider() {
			@Override
			public void reset() {}

			@Override
			public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
				return UUID.randomUUID().toString();
			}
		} );

		final String result = cut.getRetestId( createIdent() );
		assertThat( result ).isEqualTo( cut.getRetestId( createIdent() ) );

		cut.reset();

		assertThat( result ).isEqualTo( cut.getRetestId( createIdent() ) );
	}

	private IdentifyingAttributes createIdent() {
		return IdentifyingAttributes.create( fromString( "/HTML/DIV[1]" ), "DIV" );
	}
}
