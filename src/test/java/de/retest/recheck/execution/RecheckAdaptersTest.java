package de.retest.recheck.execution;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RecheckAdaptersTest {

	@Test
	void findAdapterFor_should_throw_exception_if_none_found() {
		assertThatThrownBy( () -> RecheckAdapters.findAdapterFor( new Object() ) ) //
				.isInstanceOf( UnsupportedOperationException.class ) //
				.hasMessage( "No recheck adapter registered that can handle an object of class java.lang.Object." );
	}

}
