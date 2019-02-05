package de.retest.recheck.util;

import static java.io.File.separator;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import de.retest.recheck.Properties;

public class JvmUtil {

	static final String DEBUG_OPTION = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005";

	public static String getJvm() {
		final String javaExecutable = SystemUtils.IS_OS_WINDOWS ? "javaw.exe" : "java";
		return System.getProperty( "java.home" ) + separator + "bin" + separator + javaExecutable + " ";
	}

	public static File getCurrentExecutionDir() {
		return new File( System.getProperty( "user.dir" ) );
	}

	/**
	 * Since {@code System.getProperty( "java.class.path" )} can return {@code null} under some circumstances, this code
	 * circumvents this problem by getting the used URLs from the context class loader.
	 *
	 * @return current classpath
	 */
	public static String getCurrentClasspath() {
		final StringBuilder builder = new StringBuilder();
		final URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		for ( final URL url : urlClassLoader.getURLs() ) {
			final File urlFile = new File( url.getFile() );
			builder.append( FileUtil.canonicalPathQuietly( urlFile ) + File.pathSeparator );
		}
		return builder.toString();
	}

	public static String getDebugOptions() {
		return Boolean.getBoolean( Properties.REMOTE_DEBUG ) ? DEBUG_OPTION : null;
	}

	public static String toArg( final Object key ) {
		return "-D" + key;
	}

	public static String toArg( final Object key, final Object val ) {
		return toArg( key ) + "=" + (StringUtils.containsWhitespace( val.toString() ) ? "\"" + val + "\"" : val);
	}

	public static String evalToArg( final Object key ) {
		final Object val = System.getProperty( key.toString() );
		// Empty string in command line causes a JVM error, whereas null is handled correctly.
		return val != null ? toArg( key, val ) : null;
	}

	public static int getJvmPid() {
		return Integer.parseInt( ManagementFactory.getRuntimeMXBean().getName().split( "@" )[0] );
	}

	public static List<String> getLaunchArguments() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments();
	}

	public static String findGuiJar() {
		final Class<?> clazz;
		try {
			clazz = Class.forName( "de.retest.cli.Retest" );
		} catch ( final ClassNotFoundException e ) {
			return null;
		}
		final CodeSource source = clazz.getProtectionDomain().getCodeSource();
		if ( source == null ) {
			return null;
		}
		final URL location = source.getLocation();
		if ( location == null ) {
			return null;
		}
		return FileUtils.toFile( location ).getAbsolutePath();
	}
}
