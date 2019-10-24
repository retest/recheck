package de.retest.recheck.configuration;

import static de.retest.recheck.util.FileUtil.canonicalFileQuietly;
import static de.retest.recheck.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import de.retest.recheck.Properties;
import de.retest.recheck.util.FileUtil;
import de.retest.recheck.util.JvmUtil;

class RetestWorkspace {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( RetestWorkspace.class );

	final File userConfigFile;
	final File workspaceFolder;

	RetestWorkspace( final File verifiedFileOrNull ) {
		if ( verifiedFileOrNull != null ) {
			logger.info( "Initialize ReTestWorkspace with encountered userConfigFile '{}'.",
					canonicalPathQuietly( verifiedFileOrNull ) );
		} else {
			logger.warn( "No properties file found, no user properties loaded! Specify valid system property '{}'.",
					Properties.CONFIG_FILE_PROPERTY );
		}

		userConfigFile = verifiedFileOrNull;
		workspaceFolder = findWorkspaceFolder();
	}

	String getPropertiesFileArgument() {
		if ( userConfigFile != null ) {
			return JvmUtil.toArg( Properties.CONFIG_FILE_PROPERTY, userConfigFile );
		} else {
			return "";
		}
	}

	File getPropertiesFile() {
		return userConfigFile;
	}

	static File tryToFindConfigFile() {
		File result = tryToFindConfigFileWithProperty();
		if ( result != null ) {
			return result;
		}
		result = tryToFindConfigFileInExecutionDir();
		if ( result != null ) {
			return result;
		}
		result = tryToFindConfigFileWithWorkspaceFolderProperty();
		if ( result != null ) {
			return result;
		}
		result = tryToFindConfigFileWithDefaultWorkspaceFolder();
		return result;
	}

	private static File tryToFindConfigFileWithProperty() {
		final String propertiesPath = System.getProperty( Properties.CONFIG_FILE_PROPERTY );
		if ( StringUtils.isNotBlank( propertiesPath ) ) {
			final File propertiesFile = canonicalFileQuietly( new File( propertiesPath ) );
			if ( !propertiesFile.exists() ) {
				logger.error( "Properties file '{}' as specified by system property '{}' could not be found!",
						propertiesFile, Properties.CONFIG_FILE_PROPERTY );
			} else if ( !propertiesFile.canRead() ) {
				logger.error( "Properties file '{}' as specified by system property '{}' isn't readable!",
						propertiesFile, Properties.CONFIG_FILE_PROPERTY );
			} else {
				return propertiesFile;
			}
		}
		return null;
	}

	private static File tryToFindConfigFileInExecutionDir() {
		return FileUtil.readableCanonicalFileOrNull( new File(
				System.getProperty( "user.dir" ) + File.separatorChar + Properties.RETEST_PROPERTIES_FILE_NAME ) );
	}

	private static File tryToFindConfigFileWithWorkspaceFolderProperty() {
		return FileUtil.readableCanonicalFileOrNull(
				new File( tryToFindWorkspaceFolderWithProperty(), Properties.RETEST_PROPERTIES_FILE_NAME ) );
	}

	private static File tryToFindConfigFileWithDefaultWorkspaceFolder() {
		return FileUtil
				.readableCanonicalFileOrNull( new File( getDefaultWorkDir(), Properties.RETEST_PROPERTIES_FILE_NAME ) );
	}

	//

	private File findWorkspaceFolder() {
		File result = tryToFindWorkspaceFolderWithProperty();
		if ( result != null ) {
			return result;
		}
		result = tryToFindWorkspaceFolderWithConfigFile();
		if ( result != null ) {
			return result;
		}

		result = getDefaultWorkDir();
		if ( result != null ) {
			logger.warn( "Properties {} and {} are not set, assuming default ('{}').", Properties.CONFIG_FILE_PROPERTY,
					Properties.WORK_DIRECTORY, canonicalPathQuietly( result ) );
			return result;
		}
		result = Paths.get( System.getProperty( "user.dir" ) ).toFile();
		if ( result != null ) {
			logger.warn( "No work dir could be determined, using current execution dir '{}'.", result );
			return result;
		}
		throw new IllegalStateException(
				"Looks like you have launched review via the JAR file, please use one of the native launchers: review.exe, review.bat or review." );
	}

	private static File tryToFindWorkspaceFolderWithProperty() {
		final String property = System.getProperty( Properties.WORK_DIRECTORY );
		if ( StringUtils.isNotBlank( property ) ) {
			return FileUtil.readableWriteableCanonicalDirectoryOrNull( new File( property ) );
		} else {
			return null;
		}
	}

	private File tryToFindWorkspaceFolderWithConfigFile() {
		if ( userConfigFile != null ) {
			return FileUtil.readableWriteableCanonicalDirectoryOrNull( userConfigFile.getParentFile() );
		} else {
			return null;
		}
	}

	private static File getDefaultWorkDir() {
		return Properties.getReTestInstallDir();
	}

}
