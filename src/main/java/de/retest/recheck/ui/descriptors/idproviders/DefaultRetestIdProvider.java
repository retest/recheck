package de.retest.recheck.ui.descriptors.idproviders;

import static de.retest.recheck.util.RetestIdUtil.cutTypeQualifier;
import static de.retest.recheck.util.RetestIdUtil.normalizeAndCut;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

public class DefaultRetestIdProvider implements RetestIdProvider {

	protected static final String DELIMITER = "-";

	private final Set<String> knownRetestIds = new HashSet<>();

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		if ( identifyingAttributes == null ) {
			throw new NullPointerException( "Identifying attributes must not be null." );
		}
		final String text = normalizeAndCut( identifyingAttributes.get( "text" ) );
		final String type = normalizeAndCut( cutTypeQualifier( identifyingAttributes.get( "type" ) ) );
		final String rawId = StringUtils.isNotBlank( text ) ? text : type;
		final String id = StringUtils.isNotBlank( rawId ) ? rawId : getUniqueSuffix();
		return makeUnique( id );
	}

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
