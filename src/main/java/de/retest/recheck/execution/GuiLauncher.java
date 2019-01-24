package de.retest.recheck.execution;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.configuration.Configuration;
import de.retest.util.JvmUtil;

public class GuiLauncher {

	private static final Logger logger = LoggerFactory.getLogger( GuiLauncher.class );

	public static void launchRetestGui( final File file ) {
		final String jar = JvmUtil.findGuiJar();
		if ( jar == null ) {
			logger.info( "Could not find jar to start retest GUI." );
			return;
		}
		final DefaultExecutor executor = new DaemonExecutor();
		final CommandLine commandLine = buildCommandLine( jar, file );
		try {
			executor.execute( commandLine );
		} catch ( final Throwable e ) {
			logger.warn( "Could not start retest GUI due to exception: ", e );
		}
	}

	private static CommandLine buildCommandLine( final String jar, final File file ) {
		final CommandLine cmdLine = new CommandLine( JvmUtil.getJvm() );
		cmdLine.addArgument( "-Xmx2048m" );
		cmdLine.addArgument( "-cp" );
		cmdLine.addArgument( JvmUtil.getCurrentClasspath() );
		cmdLine.addArgument( Configuration.getPropertiesFileArgument() );
		cmdLine.addArgument( "-jar" );
		cmdLine.addArgument( jar, true );
		cmdLine.addArgument( "gui" );
		cmdLine.addArgument( file.getAbsolutePath(), true );
		return cmdLine;
	}
}
