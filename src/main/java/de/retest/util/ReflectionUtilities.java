package de.retest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.retest.Properties;
import de.retest.configuration.ConfigurationException;
import de.retest.configuration.Property;

public final class ReflectionUtilities {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( ReflectionUtilities.class );

	public static List<Field> getAllFields( Class<?> clazz ) {
		final List<Field> result = new ArrayList<>();
		while ( !clazz.getName().equals( "java.lang.Object" ) ) {
			result.addAll( Arrays.asList( clazz.getDeclaredFields() ) );
			clazz = clazz.getSuperclass();
		}
		return result;
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T getFromField( final Object object, final String fieldName ) {
		final Field field = getField( object.getClass(), fieldName );
		if ( field == null ) {
			throw new RuntimeException( new NoSuchFieldException(
					"Field '" + fieldName + "' not found in class '" + object.getClass() + "' or any superclass" ) );
		}
		try {
			return (T) field.get( object );
		} catch ( final IllegalAccessException exception ) {
			throw new RuntimeException( exception );
		}
	}

	public static boolean isFieldAvailable( final Class<?> clazz, final String fieldName ) {
		boolean result = false;
		try {
			result = clazz.getDeclaredField( fieldName ) != null;
		} catch ( final SecurityException e ) {

		} catch ( final NoSuchFieldException e ) {

		}
		return result;
	}

	public static Field getField( final Class<?> clazz, final String fieldName ) {
		Field field = null;
		Class<?> currentClass = clazz;
		while ( field == null && !currentClass.getName().equals( "java.lang.Object" ) ) {
			try {
				field = currentClass.getDeclaredField( fieldName );
			} catch ( final NoSuchFieldException exc ) {
				currentClass = currentClass.getSuperclass();
			}
		}
		if ( field != null ) {
			field.setAccessible( true );
		}
		return field;
	}

	public static void setField( final Object object, final String fieldName, final Object value )
			throws IncompatibleTypesException {
		final Field field = getField( object.getClass(), fieldName );
		try {
			if ( field == null ) {
				throw new RuntimeException( new NoSuchFieldException( "Field '" + fieldName + "' not found in class '"
						+ object.getClass() + "' or any superclass" ) );
			}

			// remove final modifier from field
			final Field modifiersField = Field.class.getDeclaredField( "modifiers" );
			modifiersField.setAccessible( true );
			modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL );

			field.set( object, value );
		} catch ( final IllegalArgumentException e ) {
			throw new IncompatibleTypesException( field.getType(), value.getClass(),
					"for field '" + fieldName + "' in class '" + field.getDeclaringClass() + "'." );
		} catch ( final Exception exc ) {
			throw new RuntimeException( exc );
		}
	}

	public static void setStaticField( final Class<?> clazz, final String fieldName, final Object value ) {
		try {
			final Field field = getField( clazz, fieldName );
			if ( field == null ) {
				throw new RuntimeException( new NoSuchFieldException(
						"Field '" + fieldName + "' not found in class '" + clazz + "' or any superclass" ) );
			}

			// remove final modifier from field
			final Field modifiersField = Field.class.getDeclaredField( "modifiers" );
			modifiersField.setAccessible( true );
			modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL );

			field.set( null, value );
		} catch ( final Exception exc ) {
			throw new RuntimeException( exc );
		}
	}

	public static Method getMethod( final String className, final String methodName ) {
		try {
			final Method method = getClass( className ).getDeclaredMethod( methodName );
			method.setAccessible( true );
			return method;
		} catch ( final NoSuchMethodException exception ) {
			throw new RuntimeException( exception );
		}
	}

	public static Class<?> getClass( final String className ) {
		try {
			return ReflectionUtilities.class.getClassLoader().loadClass( className );
		} catch ( final ClassNotFoundException exception ) {
			throw new RuntimeException( exception );
		}
	}

	public static Class<?> getClassOrNull( final String className ) {
		try {
			return ReflectionUtilities.class.getClassLoader().loadClass( className );
		} catch ( final ClassNotFoundException exception ) {
			return null;
		}
	}

	public static boolean instanceOf( final String instanceClassName, final Class<?> clazz ) {
		try {
			final Class<?> instanceClass = ReflectionUtilities.class.getClassLoader().loadClass( instanceClassName );

			return instanceOf( instanceClass, clazz.getName() );
		} catch ( final ClassNotFoundException e ) {
			logger.error( "Couldn't find class '{}'.", instanceClassName, e );
		}

		return false;
	}

	public static boolean instanceOf( final Object instance, final String className ) {
		return instanceOf( instance.getClass(), className );
	}

	public static boolean instanceOf( Class<?> instanceClass, final String className ) {
		if ( className.startsWith( "class " ) ) {
			throw new IllegalArgumentException( "Class name starts with 'class '. This is probably due to the use of "
					+ "Object#toString(), whereas Class#getName() should be used." );
		}

		if ( instanceClass.getName().equals( className ) ) {
			return true;
		}

		while ( instanceClass.getSuperclass() != null ) {
			instanceClass = instanceClass.getSuperclass();

			if ( instanceClass.getName().equals( className ) ) {
				return true;
			}
		}

		return false;
	}

	public static <T> T createNewInstanceOrNull( final Class<T> assignableType, final String className ) {
		try {
			final Class<T> targetClass = getSubClassOrNull( assignableType, className );
			if ( targetClass == null ) {
				return null;
			}
			return getNonArgConstructor( targetClass ).newInstance( new Object[0] );

		} catch ( final InstantiationException exc ) {
			throw new IllegalArgumentException( "Can't create instance of an abstract class!", exc );
		} catch ( final InvocationTargetException exc ) {
			throw new RuntimeException( "Instance creation failed, constructor throws an exception!", exc );
		} catch ( final IllegalAccessException exc ) {
			throw new RuntimeException( "This shouldn't happen because the method is set accessible before!", exc );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> Class<T> getSubClass( final Class<T> clazz, final String className ) {
		final Class<?> uncheckedClass = getClass( className );
		if ( clazz.isAssignableFrom( uncheckedClass ) ) {
			return (Class<T>) uncheckedClass;
		} else {
			throw new IllegalArgumentException(
					"className '" + className + "' must be instance of class '" + clazz + "'" );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> Class<T> getSubClassOrNull( final Class<T> clazz, final String className ) {
		final Class<?> uncheckedClass = getClassOrNull( className );
		if ( uncheckedClass != null && clazz.isAssignableFrom( uncheckedClass ) ) {
			return (Class<T>) uncheckedClass;
		} else {
			return null;
		}
	}

	public static <T> Constructor<T> getNonArgConstructor( final Class<T> clazz ) {
		return getConstructor( clazz );
	}

	public static <T> Constructor<T> getConstructor( final Class<T> clazz, final Class... classes ) {
		try {
			final Constructor<T> constructor = clazz.getDeclaredConstructor( classes );
			constructor.setAccessible( true );
			return constructor;
		} catch ( final NoSuchMethodException exc ) {
			throw new IllegalArgumentException( "Class has no non-arg constructor!", exc );
		}
	}

	public static <T> T invokeConstructor( final Class<T> clazz, final Class[] classes, final Object... objects ) {
		return invokeConstructor( getConstructor( clazz, classes ), objects );
	}

	public static <T> T invokeConstructor( final Constructor<T> constructor, final Object... object ) {
		try {
			return constructor.newInstance( object );
		} catch ( final Exception e ) {
			throw new IllegalArgumentException( "Could not invoke constructor", e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T invokeMethod( final Object callee, final String methodName ) {
		return (T) invokeMethod( callee, methodName, new Class<?>[] {}, new Object[] {} );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T invokeMethod( final Object callee, final String methodName, final Class<?>[] argClasses,
			final Object[] args ) {
		try {
			try {
				final Method method = callee.getClass().getDeclaredMethod( methodName, argClasses );
				method.setAccessible( true );
				return (T) method.invoke( callee, args );
			} catch ( final NoSuchMethodException exc ) {
				final Method method = callee.getClass().getMethod( methodName, argClasses );
				return (T) method.invoke( callee, args );
			}
		} catch ( final Exception exc ) {
			throw new RuntimeException( exc );
		}
	}

	public static <T> T invokeMethod( final Object callee, final String methodName, final Object... params ) {
		final Class<?>[] paramClasses = new Class<?>[params.length];
		for ( int index = 0; index < params.length; index++ ) {
			paramClasses[index] = params[index].getClass();
		}
		return (T) invokeMethod( callee, methodName, paramClasses, params );
	}

	public static void invokeMain( final Class<?> mainClass, final String[] args ) {
		logger.debug( "Launching main class: {}.", mainClass );
		try {
			final Method mainMethod = mainClass.getMethod( "main", String[].class );
			mainMethod.setAccessible( true );
			mainMethod.invoke( null, new Object[] { args } );
		} catch ( final NoSuchMethodException exception ) {
			throw new ConfigurationException( new Property( Properties.MAIN_CLASS, mainClass.getName() ),
					"Loaded main class from '" + getSourceLocation( mainClass ) + "' doesn't provide 'main' method.",
					exception );
		} catch ( final IllegalAccessException exception ) {
			throw new ConfigurationException( new Property( Properties.MAIN_CLASS, mainClass.getName() ),
					"Cannot access 'main' method of main class from '" + getSourceLocation( mainClass ) + "'.",
					exception );
		} catch ( final InvocationTargetException exception ) {
			if ( isThreadDeathWhileClosingSuT( exception ) ) {
				logger.debug( "Ignore ThreadDeath while closing SuT." );
			} else {
				throw new ConfigurationException( new Property( Properties.MAIN_CLASS, mainClass.getName() ),
						"Invoking 'main' method failed because of: " + exception.getMessage(), exception );
			}
		}
	}

	public static boolean isThreadDeathWhileClosingSuT( final Throwable e ) {
		if ( e != null && e instanceof ThreadDeath ) {
			for ( final StackTraceElement stackElement : e.getStackTrace() ) {
				if ( "sun.awt.AppContext".equals( stackElement.getClassName() )
						&& "dispose".equals( stackElement.getMethodName() ) ) {
					return true;
				}
			}
		}
		if ( e.getCause() != null ) {
			return isThreadDeathWhileClosingSuT( e.getCause() );
		} else {
			return false;
		}
	}

	public static void clearCollectionField( final String fieldName )
			throws NoSuchFieldException, IllegalAccessException {
		final Field collectionField = DriverManager.class.getDeclaredField( fieldName );
		collectionField.setAccessible( true );
		final Collection<?> collection = (Collection<?>) collectionField.get( null );
		collection.clear();
	}

	public static String getSourceLocation( final Class<?> clazz ) {
		try {
			if ( clazz.getProtectionDomain() != null && clazz.getProtectionDomain().getCodeSource() != null
					&& clazz.getProtectionDomain().getCodeSource().getLocation() != null ) {
				return clazz.getProtectionDomain().getCodeSource().getLocation().toString();
			}
		} catch ( final Exception exc ) {
			logger.error( "Exception retrieving location of class {}.", clazz, exc );
		}
		return null;
	}

	public static boolean declaresAnyMethod( final Class<?> clazz, final String... methodNamesArray ) {
		final List<String> methodNames = Arrays.asList( methodNamesArray );
		for ( final Method method : clazz.getDeclaredMethods() ) {
			if ( methodNames.contains( method.getName() ) ) {
				return true;
			}
		}
		return false;
	}

	public static void setChildInParentToNull( final Object parent, final Object child ) {
		for ( final Field field : getAllFields( parent.getClass() ) ) {
			try {
				field.setAccessible( true );
				if ( field.get( parent ) == child ) {
					field.set( parent, null );
				}
			} catch ( final IllegalArgumentException e ) {
				throw new AssertionError( "Should not be possible!" );
			} catch ( final IllegalAccessException e ) {
				throw new AssertionError( "Should not be possible!" );
			}
		}
	}

	public static boolean isAnyAnnotationPresent( final Field field,
			final Class<? extends Annotation>... annotationClasses ) {
		for ( final Class<? extends Annotation> annotationClass : annotationClasses ) {
			if ( field.isAnnotationPresent( annotationClass ) ) {
				return true;
			}
		}
		return false;
	}

	public static class IncompatibleTypesException extends Exception {
		private static final long serialVersionUID = 1L;
		private final String expectedType;
		private final String actualType;
		private final String context;

		public IncompatibleTypesException( final Class<?> expectedType, final Class<?> actualType,
				final String context ) {
			super( "Incompatible types: expected a value of type " + expectedType + " but was " + actualType + " "
					+ context );
			this.expectedType = expectedType.getName();
			this.actualType = actualType.getName();
			this.context = context;
		}

		public String getExpectedType() {
			return expectedType;
		}

		public String getActualType() {
			return actualType;
		}

		public String getContext() {
			return context;
		}
	}

	public static String getSimpleName( final String classname ) {
		if ( classname == null || !classname.contains( "." ) ) {
			return classname;
		}
		return classname.substring( classname.lastIndexOf( "." ) + 1, classname.length() );
	}
}
