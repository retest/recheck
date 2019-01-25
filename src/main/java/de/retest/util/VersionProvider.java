package de.retest.util;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;

public class VersionProvider {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( VersionProvider.class );
	private static final String PROPERTIES_FILE_LOCATION = "/retest-defaults.properties";
	private static final String RETEST_VERSION_PROPERTY = "de.retest.version";

	public static final String RETEST_VERSION = parseRetestVersion();
	public static final String RETEST_BUILD_DATE = parseRetestBuildDate();

	private static String parseRetestVersion() {
		{
			final String packageVersion = VersionProvider.class.getPackage().getImplementationVersion();
			if ( StringUtils.isNoneBlank( packageVersion ) ) {
				return packageVersion;
			}
			logger.warn( "Failed to read package version!" );
		}

		{
			final String manifestVersion = readVersionFromManifest();
			if ( StringUtils.isNoneBlank( manifestVersion ) ) {
				return manifestVersion;
			}
			logger.warn( "Failed to read manifest version!" );
		}

		// for testing
		{
			final String pomVersion = readVersionFromProperties();
			if ( StringUtils.isNoneBlank( pomVersion ) ) {
				return pomVersion;
			}
		}

		throw new RuntimeException(
				"No version found. This seems to be a corrupted jar file: " + getLocationOfClass() );
	}

	private static String parseRetestBuildDate() {
		{
			final String manifestBuildDate = readBuildDateFromManifest();
			if ( StringUtils.isNoneBlank( manifestBuildDate ) ) {
				return manifestBuildDate;
			}
		}
		return "Build date placeholder for running in IDE";
	}

	private static String readVersionFromManifest() {
		try {
			final URLConnection jarConnection = getLocationOfClass().openConnection();
			if ( !(jarConnection instanceof JarURLConnection) ) {
				return null;
			}
			final JarURLConnection conn = (JarURLConnection) jarConnection;
			final Manifest mf = conn.getManifest();
			final Attributes atts = mf.getMainAttributes();
			return atts.getValue( "Implementation-Version" );
		} catch ( final IOException e ) {
			return null;
		}
	}

	private static String readVersionFromProperties() {
		final Properties props = new Properties();
		try {
			props.load( VersionProvider.class.getResourceAsStream( PROPERTIES_FILE_LOCATION ) );
			return props.getProperty( RETEST_VERSION_PROPERTY );
		} catch ( final IOException e ) {
			logger.error( "Exception trying to read Maven version: ", e );
			return null;
		}
	}

	private static String readBuildDateFromManifest() {
		try {
			final URLConnection jarConnection = getLocationOfClass().openConnection();
			if ( !(jarConnection instanceof JarURLConnection) ) {
				return null;
			}
			final JarURLConnection conn = (JarURLConnection) jarConnection;
			final Manifest mf = conn.getManifest();
			final Attributes atts = mf.getMainAttributes();
			return atts.getValue( "Build-Time" );
		} catch ( final IOException e ) {
			return null;
		}
	}

	private static URL getLocationOfClass() {
		return VersionProvider.class.getResource( VersionProvider.class.getSimpleName() + ".class" );
	}

}
