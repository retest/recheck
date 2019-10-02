package de.retest.recheck.ui.descriptors.idproviders;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Instead of a counting suffix, create a random suffix if there is more than one of the same element.
 */
public class RandomSuffixRetestIdProvider extends AbstractFirstNonNullRetestIdProvider {

	private final Set<String> knownRetestIds = new HashSet<>();

	@Override
	protected String makeUnique( final String id ) {
		String uniqueId = id;
		while ( knownRetestIds.contains( uniqueId ) ) {
			uniqueId = id + DELIMITER + getUniqueSuffix();
		}
		knownRetestIds.add( uniqueId );
		return uniqueId;
	}

	private String getUniqueSuffix() {
		return UUID.randomUUID().toString().substring( 0, 5 );
	}

	@Override
	public void reset() {
		knownRetestIds.clear();
	}
}
