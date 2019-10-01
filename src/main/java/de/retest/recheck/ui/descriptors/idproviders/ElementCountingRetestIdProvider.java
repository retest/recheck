package de.retest.recheck.ui.descriptors.idproviders;

import static de.retest.recheck.util.RetestIdUtil.cutTypeQualifier;
import static de.retest.recheck.util.RetestIdUtil.normalizeAndCut;
import static java.util.UUID.randomUUID;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

/**
 * Use the first non-null, non-blank of native id attribute, text or type to create a unique retestId. If the id, text
 * or type already exists, use a random suffix to make it unique.
 */
public class ElementCountingRetestIdProvider implements RetestIdProvider {

	protected static final String DELIMITER = "-";

	private final Map<String, Integer> counter = new HashMap<>();

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		if ( identifyingAttributes == null ) {
			throw new NullPointerException( "Identifying attributes must not be null." );
		}
		final String htmlId = normalizeAndCut( identifyingAttributes.get( "id" ) );
		final String text = normalizeAndCut( identifyingAttributes.get( "text" ) );
		final String type = normalizeAndCut( cutTypeQualifier( identifyingAttributes.get( "type" ) ) );
		final String id = returnFirstNonBlank( htmlId, text, type, randomUUID().toString() );
		return makeUnique( id );
	}

	private String returnFirstNonBlank( final String... args ) {
		for ( final String arg : args ) {
			if ( StringUtils.isNotBlank( arg ) ) {
				return arg;
			}
		}
		throw new IllegalStateException( "Should have at least one non-blank argument!" );
	}

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
