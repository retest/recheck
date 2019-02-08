package de.retest.recheck;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.idproviders.RetestIdProvider;

/**
 * This class is for testing only and returns retest IDs based on {@link IdentifyingAttributes#identifier()}.
 */
public class DeterministicRetestIdProvider implements RetestIdProvider {

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		return identifyingAttributes.identifier();
	}

	@Override
	public void reset() {}

}
