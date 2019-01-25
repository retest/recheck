package de.retest.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PropertyVariableResolver extends PropertiesProxy {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger( PropertyVariableResolver.class );

	private static final Pattern VARIABLES_PATTERN = Pattern.compile( ".*\\$\\{([\\w\\.]*)\\}.*" );
	static final List<String> IGNORED_KEYS =
			Arrays.asList( "awt", "com.sun", "file.encoding", "java", "os", "sun", "user" );

	PropertyVariableResolver( final java.util.Properties parent ) {
		super( parent );
	}

	@Override
	public String getProperty( final String key ) {
		return repetitiveReplaceVariables( key, super.getProperty( key ) );
	}

	@Override
	public String getProperty( final String key, final String defaultValue ) {
		return repetitiveReplaceVariables( key, super.getProperty( key, defaultValue ) );
	}

	private String repetitiveReplaceVariables( final String key, String value ) {
		if ( StringUtils.isBlank( value ) ) {
			return value;
		}

		if ( shouldIgnoreNonRetestProperty( key ) ) {
			return value;
		}

		Matcher matcher = VARIABLES_PATTERN.matcher( value );
		while ( matcher.find() ) {
			final String placeholderName = matcher.group( 1 );

			value = processReplacement( key, value, placeholderName );

			matcher = VARIABLES_PATTERN.matcher( value );
		}
		return value;
	}

	private boolean shouldIgnoreNonRetestProperty( final String key ) {
		if ( key.startsWith( "de.retest" ) ) {
			return false;
		}

		for ( final String ignoredKey : IGNORED_KEYS ) {
			if ( key.startsWith( ignoredKey ) ) {
				return true;
			}
		}

		logger.debug( "Not resolving non-retest property '{}'.", key );
		return true;
	}

	private String processReplacement( final String key, String value, final String placeholderName ) {
		final String replacement = super.getProperty( placeholderName );
		if ( replacement == null ) {
			logger.error( "The property '{}' was not defined but needed by the property '{}'.", placeholderName, key );
			value = value.replace( "${" + placeholderName + "}", placeholderName );
		} else {
			logger.debug( "Replacing '{}' in key '{}' with '{}'.", placeholderName, key, replacement );
			value = value.replace( "${" + placeholderName + "}", replacement );
		}
		return value;
	}

}
