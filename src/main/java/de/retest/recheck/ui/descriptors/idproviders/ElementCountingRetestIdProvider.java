package de.retest.recheck.ui.descriptors.idproviders;

import java.util.HashMap;
import java.util.Map;

/**
 * Use the first non-null, non-blank of native id attribute, text or type to create a unique retestId. If the id, text
 * or type already exists, use a random suffix to make it unique.
 */
public final class ElementCountingRetestIdProvider extends AbstractFirstNonNullRetestIdProvider {

	private final Map<String, Integer> counter = new HashMap<>();

	@Override
	protected String makeUnique( final String id ) {
		final Integer result = counter.get( id );
		if ( result == null ) {
			counter.put( id, 1 );
			return id;
		}
		counter.put( id, result + 1 );
		return id + DELIMITER + result;
	}

	@Override
	public void reset() {
		counter.clear();
	}

}
