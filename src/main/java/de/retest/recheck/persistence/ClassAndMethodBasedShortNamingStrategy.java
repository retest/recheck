package de.retest.recheck.persistence;

/**
 * Extends the standard {@link ClassAndMethodBasedNamingStrategy}, but instead of the qualified class name (e.g.
 * `com.mycompany.MyTest`) returns the {@link Class#getSimpleName()} of the class (e.g. `MyTest`).
 */
public class ClassAndMethodBasedShortNamingStrategy extends ClassAndMethodBasedNamingStrategy {

	/**
	 * @return The {@link Class#getSimpleName()} of the test class (e.g. `MyTest`).
	 */
	@Override
	public String getSuiteName() {
		String result = super.getSuiteName();
		if ( result.contains( "." ) ) {
			result = result.substring( result.lastIndexOf( "." ) + 1 );
		}
		if ( result.contains( "$" ) ) {
			result = result.substring( 0, result.indexOf( "$" ) );
		}
		return result;
	}
}
