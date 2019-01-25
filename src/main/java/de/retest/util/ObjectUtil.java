package de.retest.util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ObjectUtil {

	private ObjectUtil() {}

	private static final Pattern TOSTRING_OBJECT_ID_PATTERN = Pattern.compile( "([\\.\\w]+@([0-9a-fA-F]{4,8}|1))" );

	public static <T> T checkNull( final T object, final String name ) {
		if ( object == null ) {
			throw new NullPointerException( name + " must not be null!" );
		}
		return object;
	}

	public static int compare( final int previousResult, final Serializable localAttr, final Serializable otherAttr ) {
		if ( previousResult != 0 ) {
			return previousResult;
		}
		if ( localAttr != null ) {
			if ( otherAttr != null ) {
				return localAttr.toString().compareTo( otherAttr.toString() );
			}
			return 1;
		}
		if ( otherAttr != null ) {
			return -1;
		}
		return 0;
	}

	public static int nextHashCode( final int previousHash, final Serializable attr ) {
		if ( attr == null ) {
			return previousHash;
		}
		return previousHash + attr.hashCode() ^ 31;
	}

	public static boolean isObjectToString( final String text ) {
		if ( text == null ) {
			return false;
		}
		final Matcher matcher = TOSTRING_OBJECT_ID_PATTERN.matcher( text );
		return matcher.find();
	}

	public static String removeObjectHash( final String text ) {
		if ( text == null ) {
			return null;
		}
		if ( ObjectUtil.isObjectToString( text ) ) {
			return text.substring( 0, text.indexOf( '@' ) );
		}
		return text;
	}

	public static boolean isNullOrEmptyString( final Object object ) {
		return object == null || object instanceof CharSequence && StringUtils.isBlank( (CharSequence) object );
	}
}
