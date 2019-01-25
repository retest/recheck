package de.retest.util;

import de.retest.ui.descriptors.IdentifyingAttributes;

public class RetestIdUtil {

	public static class InvalidRetestIdException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public InvalidRetestIdException( final String message ) {
			super( message );
		}

	}

	private RetestIdUtil() {}

	public static String normalizeAndCut( final String id ) {
		return cut( normalize( id ) );
	}

	static String normalize( String id ) {
		if ( id == null || id.isEmpty() ) {
			return "";
		}
		// trime and replace all whitespace chars with _
		id = id.trim().replaceAll( "[\\s]", "_" );
		// remove all chars but a-z in any case, 0-9 or _
		id = id.replaceAll( "[^\\w]", "" );
		// remove long blanks
		while ( id.contains( "__" ) ) {
			id = id.replaceAll( "__", "_" );
		}
		return id.toLowerCase();
	}

	static String cut( final String id ) {
		if ( id == null || id.isEmpty() ) {
			return "";
		}
		if ( id.length() <= 17 ) {
			return id;
		}
		int blank = id.indexOf( '_' );
		while ( blank < 12 ) {
			final int nextBlank = id.indexOf( '_', blank + 1 );
			if ( nextBlank < 17 && nextBlank > -1 ) {
				blank = nextBlank;
			} else {
				break;
			}
		}
		if ( blank >= 12 && blank <= 17 ) {
			// if possible, use first word
			return id.substring( 0, blank );
		}
		return id.substring( 0, 15 );
	}

	public static boolean isValid( final String retestId ) {
		try {
			validate( retestId, null );
			return true;
		} catch ( final InvalidRetestIdException e ) {
			return false;
		}
	}

	public static void validate( final String retestId, final IdentifyingAttributes identifyingAttributes )
			throws InvalidRetestIdException {
		if ( retestId == null ) {
			throw new InvalidRetestIdException( "retest ID must not be null for " + identifyingAttributes );
		}
		if ( retestId.isEmpty() ) {
			throw new InvalidRetestIdException( "retest ID must not be empty for " + identifyingAttributes );
		}
		if ( !retestId.matches( "[\\w-_]+" ) ) {
			throw new InvalidRetestIdException(
					"retest ID must not contain any whitespaces or special characters for " + identifyingAttributes );
		}
	}

	public static String cutTypeQualifier( final String type ) {
		if ( type == null ) {
			return "";
		}
		return type.substring( type.lastIndexOf( '.' ) + 1 );
	}

}
