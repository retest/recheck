package de.retest.recheck.ignore;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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

	private static final String JS_ENGINE_NAME = "JavaScript";

	private final ScriptEngine engine;
	private final Set<String> errorFunctions = new HashSet<>();

	public JSFilterImpl( final Path ignoreFilePath ) {
		final ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName( JS_ENGINE_NAME );
		try {
			engine.eval( readScriptFile( ignoreFilePath ) );
		} catch ( final Exception e ) {
			logger.error( "Reading script file '{}' caused exception: ", ignoreFilePath, e );
		}
	}

	Reader readScriptFile( final Path ignoreFilePath ) {
		try {
			logger.info( "Reading JS ignore rules file from {}.", ignoreFilePath );
			return Files.newBufferedReader( ignoreFilePath, StandardCharsets.UTF_8 );
		} catch ( final Exception e ) {
			logger.error( "Error opening JS file from '{}': ", ignoreFilePath, e );
			return new StringReader( "" );
		}
	}

	@Override
	public boolean matches( final Element element ) {
		return callBooleanJSFunction( "matches", element ) || callBooleanJSFunction( "shouldIgnoreElement", element );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return callBooleanJSFunction( "matches", element, attributeDifference )
				|| callBooleanJSFunction( "shouldIgnoreAttributeDifference", element, attributeDifference );
	}

	private boolean callBooleanJSFunction( final String functionName, final Object... args ) {
		if ( errorFunctions.contains( functionName ) ) {
			return false;
		}
		final Invocable inv = (Invocable) engine;
		// call function from script file
		try {
			final Object callResult = inv.invokeFunction( functionName, args );
			if ( callResult == null ) {
				logger.warn( "{} returned 'null' instead of a boolean value. Interpreting that as 'false'.",
						functionName );
				return false;
			}
			if ( !(callResult instanceof Boolean) ) {
				logger.error( "'{}' of {} cannot be cast to java.lang.Boolean.", callResult, callResult.getClass() );
				errorFunctions.add( functionName );
			}
			return (boolean) callResult;
		} catch ( final ScriptException e ) {
			logger.error( "JS '{}' method caused an exception: {}", functionName, e.getMessage() );
			errorFunctions.add( functionName );
		} catch ( final NoSuchMethodException e ) {
			if ( !functionName.startsWith( "shouldIgnore" ) ) {
				logger.warn( "Specified JS ignore file has no '{}' function.", functionName );
				errorFunctions.add( functionName );
			}
		}
		return false;
	}
}
