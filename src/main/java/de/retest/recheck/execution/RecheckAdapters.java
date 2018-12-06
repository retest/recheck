package de.retest.recheck.execution;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import de.retest.recheck.RecheckAdapter;

public class RecheckAdapters {

	private static final ServiceLoader<RecheckAdapter> adapters = ServiceLoader.load( RecheckAdapter.class );

	public static RecheckAdapter findAdapterFor( final Object toVerify ) {
		return StreamSupport.stream( adapters.spliterator(), false ) //
				.filter( adapter -> adapter.canCheck( toVerify ) ) //
				.findAny() //
				.orElseThrow( () -> new UnsupportedOperationException( String.format(
						"No recheck adapter registered that can handle an object of %s.", toVerify.getClass() ) ) );
	}

}
