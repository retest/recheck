package de.retest.recheck.ui.descriptors.idproviders;

import java.util.HashMap;
import java.util.Map;

public class ElementCountingRetestIdProvider extends DefaultRetestIdProvider {

	private final Map<String, Integer> counter = new HashMap<>();

	@Override
	protected String makeUnique( final String id ) {
		Integer result = counter.get( id );
		if ( result == null ) {
			result = 1;
		}
		counter.put( id, result + 1 );
		return id + DELIMITER + result;
	}

	@Override
	public void reset() {
		super.reset();
		counter.clear();
	}

}
