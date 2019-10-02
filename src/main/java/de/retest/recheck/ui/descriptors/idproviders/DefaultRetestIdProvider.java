package de.retest.recheck.ui.descriptors.idproviders;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

public class DefaultRetestIdProvider implements RetestIdProvider {

	private final RetestIdProvider delegate = new ElementCountingRetestIdProvider();

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		return delegate.getRetestId( identifyingAttributes );
	}

	@Override
	public void reset() {
		delegate.reset();
	}
}
