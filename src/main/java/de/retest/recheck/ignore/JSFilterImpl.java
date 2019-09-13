package de.retest.recheck.ignore;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class JSFilterImpl implements Filter {

	private static final Logger logger = LoggerFactory.getLogger( JSFilterImpl.class );

	private static final String JS_ENGINE_NAME = "rhino";

	private final String filePath;
	private final ScriptEngine engine;
	private boolean noMethodWarningPrinted = false;

	public JSFilterImpl( final Path filterFilePath ) {
		filePath = filterFilePath.toString();
		final ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName( JS_ENGINE_NAME );
		if ( engine == null ) {
			logger.error(
					"Cannot execute JavaScript rules, `ScriptEngineManager` returned null for engine `JavaScript`. Try switching JDK or add a JavaScript engine: ",
					new IllegalStateException( "No JavaScript available." ) );
			return;
		}
		try {
			engine.eval( readScriptFile( filterFilePath ) );
		} catch ( final Exception e ) {
			logger.error( "Reading script file '{}' caused exception: ", filterFilePath, e );
		}
	}

	Reader readScriptFile( final Path filterFilePath ) {
		try {
			logger.info( "Reading JS filter rules file from '{}'.", filterFilePath );
			return Files.newBufferedReader( filterFilePath, StandardCharsets.UTF_8 );
		} catch ( final NoSuchFileException e ) {
			logger.warn( "No JS filter file found at '{}': {}", filterFilePath, e.getMessage() );
		} catch ( final Exception e ) {
			logger.error( "Error opening JS file from '{}': ", filterFilePath, e );
		}
		return new StringReader( "" );
	}

	@Override
	public boolean matches( final Element element ) {
		return callBooleanJSFunction( "matches", element ) || callBooleanJSFunction( "shouldIgnoreElement", element );
	}

	@Override
	public boolean matches( final Element element, final ChangeType change ) {
		// "shouldIgnore" is legacy, so this needn't be called there
		return callBooleanJSFunction( "matches", element, change.toString().toLowerCase() );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return callBooleanJSFunction( "matches", element, attributeDifference )
				|| callBooleanJSFunction( "shouldIgnoreAttributeDifference", element, attributeDifference );
	}

	private boolean callBooleanJSFunction( final String functionName, final Object... args ) {
		if ( engine == null ) {
			return false;
		}
		final Invocable inv = (Invocable) engine;
		// call function from script file
		try {
			final Object callResult = inv.invokeFunction( functionName, args );
			if ( callResult == null ) {
				logger.warn( "{} returned 'null' instead of a boolean value in file '{}'. Interpreting that as 'false'.",
						filePath, functionName );
				return false;
			}
			if ( !(callResult instanceof Boolean) ) {
				logger.error( "'{}' of {} cannot be cast to java.lang.Boolean in file '{}'.", callResult,
						callResult.getClass(), filePath );
				return false;
			}
			return (boolean) callResult;
		} catch ( final ScriptException e ) {
			logger.error( "JS '{}' method caused an exception: {} in file '{}'.", functionName, e.getMessage(),
					filePath );
		} catch ( final NoSuchMethodException e ) {
			if ( !functionName.startsWith( "shouldIgnore" ) && !noMethodWarningPrinted ) {
				logger.warn( "Specified JS filter file '{}' has no '{}' function.", filePath, functionName );
				noMethodWarningPrinted = true;
			}
		}
		return false;
	}
}
