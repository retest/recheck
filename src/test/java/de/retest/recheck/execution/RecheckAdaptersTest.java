package de.retest.recheck.execution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RecheckAdaptersTest {

	@Test
	void findAdapterFor_should_throw_exception_if_none_found() {
		assertThatThrownBy( () -> RecheckAdapters.findAdapterFor( new Object() ) ) //
				.isInstanceOf( UnsupportedOperationException.class ) //
				.hasMessage( "No recheck adapter registered that can handle an object of class java.lang.Object." );
	}

	@Test
	void findAdapterFor_should_throw_helpful_message_for_common_errors() {
		assertThat( RecheckAdapters.createHelpfulExceptionForMissingAdapter( "org.openqa.selenium.chrome.ChromeDriver" )
				.getMessage() ).isEqualTo(
						"No recheck adapter registered that can handle an object of class org.openqa.selenium.chrome.ChromeDriver.\n "
								+ "You need to add recheck-web (https://github.com/retest/recheck-web) to the classpath." );
	}

}
