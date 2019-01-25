package de.retest.configuration;

import static de.retest.util.FileUtil.canonicalFileQuietly;
import static de.retest.util.FileUtil.canonicalPathQuietly;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import de.retest.Properties;
import de.retest.util.FileUtil;
import de.retest.util.JvmUtil;

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
					Configuration.PROP_CONFIG_FILE_PATH );
		}

		userConfigFile = verifiedFileOrNull;
		workspaceFolder = findWorkspaceFolder();
	}

	String getPropertiesFileArgument() {
		if ( userConfigFile != null ) {
			return JvmUtil.toArg( Configuration.PROP_CONFIG_FILE_PATH, userConfigFile );
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
		final String propertiesPath = System.getProperty( Configuration.PROP_CONFIG_FILE_PATH );
		if ( StringUtils.isNotBlank( propertiesPath ) ) {
			final File propertiesFile = canonicalFileQuietly( new File( propertiesPath ) );
			if ( !propertiesFile.exists() ) {
				logger.error( "Properties file '{}' as specified by system property '{}' could not be found!",
						propertiesFile, Configuration.PROP_CONFIG_FILE_PATH );
			} else if ( !propertiesFile.canRead() ) {
				logger.error( "Properties file '{}' as specified by system property '{}' isn't readable!",
						propertiesFile, Configuration.PROP_CONFIG_FILE_PATH );
			} else {
				return propertiesFile;
			}
		}
		return null;
	}

	private static File tryToFindConfigFileInExecutionDir() {
		return FileUtil.readableCanonicalFileOrNull( new File(
				System.getProperty( "user.dir" ) + File.separatorChar + Configuration.RETEST_PROPERTIES_FILE_NAME ) );
	}

	private static File tryToFindConfigFileWithWorkspaceFolderProperty() {
		return FileUtil.readableCanonicalFileOrNull(
				new File( tryToFindWorkspaceFolderWithProperty(), Configuration.RETEST_PROPERTIES_FILE_NAME ) );
	}

	private static File tryToFindConfigFileWithDefaultWorkspaceFolder() {
		return FileUtil.readableCanonicalFileOrNull(
				new File( getDefaultWorkDir(), Configuration.RETEST_PROPERTIES_FILE_NAME ) );
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
		logger.warn( "Properties {} and {} are not set, assuming default ('{}').", Configuration.PROP_CONFIG_FILE_PATH,
				Properties.WORK_DIRECTORY, canonicalPathQuietly( result ) );
		return result;
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
