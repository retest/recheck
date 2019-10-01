package de.retest.recheck.ui.descriptors.idproviders;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

/**
 * Instead of a counting suffix, create a random suffix if there is more than one of the same element.
 */
public class RandomSuffixRetestIdProvider implements RetestIdProvider {

	private final Set<String> knownRetestIds = new HashSet<>();
	private final Delegate delegate = new Delegate();

	private class Delegate extends ElementCountingRetestIdProvider {
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
	}

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		return delegate.getRetestId( identifyingAttributes );
	}

	@Override
	public void reset() {
		delegate.reset();
		knownRetestIds.clear();
	}
}
