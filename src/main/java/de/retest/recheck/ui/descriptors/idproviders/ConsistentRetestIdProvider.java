package de.retest.recheck.ui.descriptors.idproviders;

import java.util.HashMap;
import java.util.Map;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

public final class ConsistentRetestIdProvider implements RetestIdProvider {

	private final RetestIdProvider delegate;
	private final Map<String, String> consistency = new HashMap<>();

	public ConsistentRetestIdProvider( final RetestIdProvider delegate ) {
		this.delegate = delegate;
	}

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		String result = consistency.get( identifyingAttributes.identifier() );
		if ( result != null ) {
			return result;
		}
		result = delegate.getRetestId( identifyingAttributes );
		while ( consistency.containsValue( result ) ) {
			result = delegate.getRetestId( identifyingAttributes );
		}
		consistency.put( identifyingAttributes.identifier(), result );
		return result;
	}

	@Override
	public void reset() {
		// We do not reset our consistency, this is the whole point of it...
		delegate.reset();
	}
}
