package de.retest.recheck.ignore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.retest.Properties;

/**
 * General interface to ignore changes during Matching
 *
 * If there are irrelevant attributes that differ, they can confuse the matching algorithm. E.g. for many web pages, the
 * ID attribute is a great identifier. But with some frameworks, the ID attribute is generated randomly. When
 * additional, "real" changes come on top, this can sometimes confuse the matching algorithm. So in those cases, you
 * want to ignore the ID attribute during matching. However, these attributes are usually ignored globally.
 *
 * For more details, see <a href="https://github.com/retest/recheck/wiki/How-Ignore-works-in-recheck" target="_top">the
 * GitHub wiki</a>.
 */
public class GloballyIgnoredAttributes {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( GloballyIgnoredAttributes.class );

	public static final String IGNORED_ATTRIBUTES_PROPERTY = "de.retest.recheck.ignore.attributes";
	private static final List<String> ignoredGlobal =
			Arrays.asList( System.getProperty( IGNORED_ATTRIBUTES_PROPERTY, "" ).split( Properties.VALUES_SEPARATOR ) );

	private static GloballyIgnoredAttributes instance;

	public static GloballyIgnoredAttributes getInstance() {
		if ( instance == null ) {
			instance = new GloballyIgnoredAttributes( ignoredGlobal );
		}
		return instance;
	}

	public static GloballyIgnoredAttributes getTestInstance() {
		return getTestInstance( new HashSet<String>() );
	}

	/**
	 * Testing only!
	 *
	 * @param ignoredAttributes
	 *            attributes collection to be used
	 * @return instance with given element collection
	 */
	public static GloballyIgnoredAttributes getTestInstance( final Collection<String> ignoredAttributes ) {
		logger.warn( "Testing only! Creating GloballyIgnoredAttributes from given attributes collection." );
		instance = new GloballyIgnoredAttributes( ignoredAttributes );
		return instance;
	}

	private final Set<String> ignoredAttributes = new HashSet<>();

	private GloballyIgnoredAttributes( final Collection<String> ignoredAttributes ) {
		this.ignoredAttributes.addAll( ignoredAttributes );
	}

	public boolean shouldIgnoreAttribute( final String key ) {
		return ignoredAttributes.contains( key );
	}

	public List<String> getIgnoredAttributesList() {
		return new ArrayList<>( ignoredAttributes );
	}
}
