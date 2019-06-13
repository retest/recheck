package de.retest.recheck.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RetestAuthenticationTest {

	@Test
	void getRequestParameters_should_be_valid() throws Exception {
		final String request = "GET " + "/?state=37a6d043-e49c-4aaa-a6e3-580c29a5f706&"
				+ "session_state=3d025532-7ba4-4611-bb86-b51352605e74&"
				+ "code=5b0893dd-32bb-4853-be3f-60f72ddf6635.3d025532-7ba4-4611-bb86-b51352605e74.ca0086b2-de26-4dfb-a5a0-b9588bbc77ac"
				+ " HTTP/1.1";

		final KeycloakResult sut = RetestAuthentication.getRequestParameters( request );

		assertThat( sut.getState() ).isEqualTo( "37a6d043-e49c-4aaa-a6e3-580c29a5f706" );
		assertThat( sut.getCode() ).isEqualTo(
				"5b0893dd-32bb-4853-be3f-60f72ddf6635.3d025532-7ba4-4611-bb86-b51352605e74.ca0086b2-de26-4dfb-a5a0-b9588bbc77ac" );
		assertThat( sut.getError() ).isNull();
		assertThat( sut.getErrorDescription() ).isNull();
		assertThat( sut.getErrorException() ).isNull();
	}

}
