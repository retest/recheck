package de.retest.recheck.ui.descriptors.idproviders;

import static de.retest.recheck.ui.Path.fromString;
import static de.retest.recheck.ui.descriptors.IdentifyingAttributes.create;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

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

	@Test
	public void new_ids_should_not_be_doublettes() {
		final RetestIdProvider cut = new ConsistentRetestIdProvider( new RetestIdProvider() {
			int cnt = 0;

			@Override
			public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
				return Integer.toString( cnt++ );
			}

			@Override
			public void reset() {
				cnt = 0;
			}

		} );

		final String result = cut.getRetestId( create( fromString( "/HTML/DIV[1]" ), "DIV" ) );
		assertThat( result ).isEqualTo( cut.getRetestId( create( fromString( "/HTML/DIV[1]" ), "DIV" ) ) );

		cut.reset();

		assertThat( result ).isNotEqualTo( cut.getRetestId( create( fromString( "/HTML/DIV[2]" ), "DIV" ) ) );
	}

	private IdentifyingAttributes createIdent() {
		return create( fromString( "/HTML/DIV[1]" ), "DIV" );
	}
}
