package de.retest.recheck.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtilities {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( ReflectionUtilities.class );

	public static List<Field> getAllFields( Class<?> clazz ) {
		final List<Field> result = new ArrayList<>();
		while ( !clazz.equals( Object.class ) ) {
			result.addAll( Arrays.asList( clazz.getDeclaredFields() ) );
			clazz = clazz.getSuperclass();
		}
		return result;
	}

	public static Field getField( final Class<?> clazz, final String fieldName ) {
		Field field = null;
		Class<?> currentClass = clazz;
		while ( field == null && !currentClass.equals( Object.class ) ) {
			try {
				field = currentClass.getDeclaredField( fieldName );
			} catch ( final NoSuchFieldException exc ) {
				currentClass = currentClass.getSuperclass();
			}
		}
		if ( field == null ) {
			throw new RuntimeException( new NoSuchFieldException(
					"Field '" + fieldName + "' not found in class '" + clazz + "' or any superclass" ) );
		}
		field.setAccessible( true );
		return field;
	}

	public static void setField( final Object object, final String fieldName, final Object value )
			throws IncompatibleTypesException {
		final Field field = getField( object.getClass(), fieldName );
		try {
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
			// remove final modifier from field
			final Field modifiersField = Field.class.getDeclaredField( "modifiers" );
			modifiersField.setAccessible( true );
			modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL );

			field.set( null, value );
		} catch ( final Exception exc ) {
			throw new RuntimeException( exc );
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

	public static boolean isThreadDeathWhileClosingSuT( final Throwable e ) {
		if ( e instanceof ThreadDeath ) {
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

	public static class IncompatibleTypesException extends Exception {
		private static final long serialVersionUID = 1L;
		private final String expectedType;
		private final String actualType;

		public IncompatibleTypesException( final Class<?> expectedType, final Class<?> actualType,
				final String context ) {
			super( "Incompatible types: expected a value of type " + expectedType + " but was " + actualType + " "
					+ context );
			this.expectedType = expectedType.getName();
			this.actualType = actualType.getName();
		}

	}

	public static String getSimpleName( final String classname ) {
		if ( classname == null || !classname.contains( "." ) ) {
			return classname;
		}
		return classname.substring( classname.lastIndexOf( "." ) + 1, classname.length() );
	}
}
