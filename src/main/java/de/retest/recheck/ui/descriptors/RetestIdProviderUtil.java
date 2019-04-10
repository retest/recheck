package de.retest.recheck.ui.descriptors;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.ui.descriptors.idproviders.DefaultRetestIdProvider;
import de.retest.recheck.ui.descriptors.idproviders.RetestIdProvider;
import de.retest.recheck.util.RetestIdUtil;

public class RetestIdProviderUtil {

	private static final Logger logger = LoggerFactory.getLogger( RetestIdProviderUtil.class );

	private static RetestIdProvider configured;

	public static RetestIdProvider getConfiguredRetestIdProvider() {
		// create configured if not already set or in development mode
		if ( configured == null || Boolean.getBoolean( "de.retest.Development" ) ) {
			configured = createConfigured();
		}
		return configured;
	}

	protected static RetestIdProvider createConfigured() {
		final String configuredClassName = System.getProperty( RetestIdProvider.ID_PROVIDER_CONFIGURATION_PROPERTY );
		if ( !StringUtils.isBlank( configuredClassName ) ) {
			try {
				final Class<?> configuredClass = Class.forName( configuredClassName );
				return (RetestIdProvider) configuredClass.newInstance();
			} catch ( final Exception e ) {
				logger.error(
						"Error instantiating configured retest ID provider '{}' (with property '{}'), falling back to default.",
						configuredClassName, RetestIdProvider.ID_PROVIDER_CONFIGURATION_PROPERTY, e );
			}
		}
		return new DefaultRetestIdProvider();
	}

	public static String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		return getRetestId( getConfiguredRetestIdProvider(), identifyingAttributes );
	}

	/**
	 * Testing only.
	 *
	 * @param idProvider
	 *            retest ID provider to be used
	 * @param identifyingAttributes
	 *            identifying attributes to create a retest ID for
	 * @return created retest ID
	 */
	static String getRetestId( final RetestIdProvider idProvider, final IdentifyingAttributes identifyingAttributes ) {
		final String retestId = idProvider.getRetestId( identifyingAttributes );
		if ( RetestIdUtil.isValid( retestId ) ) {
			return retestId;
		} else {
			logger.debug( "Invalid retest ID '{}' from '{}', using a random UUID as fallback.", retestId,
					idProvider.getClass().getName() );
			return UUID.randomUUID().toString();
		}
	}

}
