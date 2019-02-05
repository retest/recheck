package de.retest.recheck.util.junit.jupiter;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Properties;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class SystemPropertyExtension
		implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

	private static final String BACKUP_STORE_KEY = "backup";

	@Override
	public void beforeEach( final ExtensionContext context ) {
		backupAndPrepareIfSystemProperty( context, context.getRequiredTestMethod() );
	}

	@Override
	public void afterEach( final ExtensionContext context ) {
		restoreIfSystemProperty( context, context.getRequiredTestMethod() );
	}

	@Override
	public void beforeAll( final ExtensionContext context ) {
		backupAndPrepareIfSystemProperty( context, context.getRequiredTestClass() );
	}

	@Override
	public void afterAll( final ExtensionContext context ) {
		restoreIfSystemProperty( context, context.getRequiredTestClass() );
	}

	private void backupAndPrepareIfSystemProperty( final ExtensionContext context, final AnnotatedElement element ) {
		if ( hasSystemProperty( element ) ) {
			backup( context, element );
			prepare( element );
		}
	}

	private void restoreIfSystemProperty( final ExtensionContext context, final AnnotatedElement element ) {
		if ( hasSystemProperty( element ) ) {
			restore( context, element );
		}
	}

	private void backup( final ExtensionContext context, final Object key ) {
		final Properties backup = new Properties();
		backup.putAll( System.getProperties() );
		final Store store = context.getStore( Namespace.create( getClass(), key ) );
		store.put( BACKUP_STORE_KEY, backup );
	}

	private void restore( final ExtensionContext context, final Object key ) {
		final Store store = context.getStore( Namespace.create( getClass(), key ) );
		final Properties backup = store.get( BACKUP_STORE_KEY, Properties.class );
		System.setProperties( backup );
	}

	private boolean hasSystemProperty( final AnnotatedElement annotatedElement ) {
		return annotatedElement.isAnnotationPresent( SystemProperty.class )
				|| annotatedElement.isAnnotationPresent( SystemProperties.class );
	}

	private void prepare( final AnnotatedElement annotatedElement ) {
		if ( annotatedElement.isAnnotationPresent( SystemProperty.class ) ) {
			final SystemProperty prop = annotatedElement.getAnnotation( SystemProperty.class );
			prepareSystemProperty( prop );
		} else if ( annotatedElement.isAnnotationPresent( SystemProperties.class ) ) {
			final SystemProperties props = annotatedElement.getAnnotation( SystemProperties.class );
			Arrays.stream( props.value() ).forEach( this::prepareSystemProperty );
		}
	}

	private void prepareSystemProperty( final SystemProperty prop ) {
		if ( prop.value().isEmpty() ) {
			System.clearProperty( prop.key() );
		} else {
			System.setProperty( prop.key(), prop.value() );
		}
	}
}
