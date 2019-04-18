package de.retest.recheck.execution;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import de.retest.recheck.RecheckAdapter;

public class RecheckAdapters {

	private static final ServiceLoader<RecheckAdapter> adapters = ServiceLoader.load( RecheckAdapter.class );

	private RecheckAdapters() {}

	public static RecheckAdapter findAdapterFor( final Object toVerify ) {
		return StreamSupport.stream( adapters.spliterator(), false ) //
				.filter( adapter -> adapter.canCheck( toVerify ) ) //
				.findAny() //
				.orElseThrow( () -> createHelpfulExceptionForMissingAdapter( toVerify.getClass().getName() ) );
	}

	protected static UnsupportedOperationException createHelpfulExceptionForMissingAdapter( final String className ) {
		final String msg =
				String.format( "No recheck adapter registered that can handle an object of class %s.", className );
		if ( className.startsWith( "org.openqa.selenium" ) ) {
			return new UnsupportedOperationException(
					msg + "\n You need to add recheck-web (https://github.com/retest/recheck-web) to the classpath." );
		}
		return new UnsupportedOperationException( msg );
	}

}
