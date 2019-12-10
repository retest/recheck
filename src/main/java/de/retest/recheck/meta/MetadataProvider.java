package de.retest.recheck.meta;

import java.util.Collections;
import java.util.Map;

/**
 * <p>
 * Provides metadata for corresponding data. The data provided may be arbitrarily complex, but should note the
 * limitations below and the limitations of the corresponding implementation in which this provider is used.
 * 
 * <p>
 * This provider does not know any context and therefore must contain such context within the implementation (i.e.
 * passed into the constructor).
 * 
 * <pre>
 * new MetadataProviderImpl( context1, context2 );
 * </pre>
 * 
 * <p>
 * This provider may be used with other providers to construct a more extensive metadata object. Therefore following
 * rules apply:
 * <ul>
 * <li><b>No {@code null} values are allowed.</b> Keys that map to non existent values (i.e. {@code null} should not be
 * added as this may be used as indicator for a missing key. Instead an other representation (e.g. {@code ""}) should be
 * used.</li>
 * <li><b>Only simple values shall be used.</b> While theoretically any arbitrary data may be provided, it should be
 * taken care, that each datum is as simple as possible. It should therefore be avoided to parse complex data from a
 * single datum. Instead, multiple correlated keys shall be used and then constructed as necessary by retrieving the
 * corresponding keys. For instance, a rectangle should be split up into its raw attributes: x, y, width, height and
 * saved separately.</li>
 * </ul>
 */
@FunctionalInterface
public interface MetadataProvider {

	/**
	 * Collects the metadata into a plain map. This map does not allow for {@code null} values or duplicate keys.
	 * 
	 * @return The metadata as plain map with key-value pairs.
	 * 
	 * @implSpec Once returned the map shall not be updated.
	 */
	Map<String, String> retrieve();

	static MetadataProvider empty() {
		return Collections::emptyMap;
	}
}
