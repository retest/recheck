package de.retest.recheck.ui.descriptors.idproviders;

import static de.retest.recheck.util.RetestIdUtil.cutTypeQualifier;
import static de.retest.recheck.util.RetestIdUtil.normalizeAndCut;
import static java.util.UUID.randomUUID;

import org.apache.commons.lang3.StringUtils;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

abstract class AbstractFirstNonNullRetestIdProvider implements RetestIdProvider {

	protected static final String DELIMITER = "-";

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

	protected abstract String makeUnique( String id );
}
