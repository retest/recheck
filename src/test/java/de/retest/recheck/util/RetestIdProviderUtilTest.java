package de.retest.recheck.util;

import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.idproviders.DefaultRetestIdProvider;
import de.retest.recheck.ui.descriptors.idproviders.RetestIdProvider;
import de.retest.recheck.util.RetestIdProviderUtil;
import de.retest.recheck.util.RetestIdUtil;

class RetestIdProviderUtilTest {

	// since we cannot use retest-testutils
	Properties systemProperties;

	@BeforeEach
	void setUp() {
		systemProperties = System.getProperties();
	}

	@Test
	void invalid_retest_id_from_provider_should_result_in_valid_fallback_id() throws Exception {
		final String invalidId = null;
		final RetestIdProvider idProvider = mock( RetestIdProvider.class );
		when( idProvider.getRetestId( any( IdentifyingAttributes.class ) ) ).thenReturn( invalidId );

		final String fallbackId = RetestIdProviderUtil.getRetestId( idProvider, mock( IdentifyingAttributes.class ) );

		assertThat( RetestIdUtil.isValid( invalidId ) ).isFalse();
		assertThat( RetestIdUtil.isValid( fallbackId ) ).isTrue();
	}

	@Test
	void invalid_retestid_config_should_fallback_to_default() {
		System.setProperty( RetestIdProvider.RETEST_ID_PROVIDER_PROPERTY, "not existent" );
		final RetestIdProvider fallback = RetestIdProviderUtil.createConfigured();
		assertThat( fallback ).isNotNull();
		assertThat( fallback.getClass().getName() ).isEqualTo( DefaultRetestIdProvider.class.getName() );
	}

	@Test
	void retest_id_should_be_unique() {
		System.setProperty( "de.retest.Development", "false" );
		final IdentifyingAttributes identifyingAttributes =
				IdentifyingAttributes.create( fromString( "/HTML[1]/DIV[1]/A[1]" ), "A" );
		final String first = RetestIdProviderUtil.getRetestId( identifyingAttributes );
		final String second = RetestIdProviderUtil.getRetestId( identifyingAttributes );
		assertThat( first ).isNotEqualTo( second );
	}

	@AfterEach
	void tearDown() {
		System.setProperties( systemProperties );
	}

}
