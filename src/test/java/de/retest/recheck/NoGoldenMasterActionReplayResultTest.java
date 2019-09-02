package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;

class NoGoldenMasterActionReplayResultTest {

	@Test
	void hasDifference_should_always_return_true() {
		final RootElement rootElement = mock( RootElement.class, RETURNS_MOCKS );

		final SutState sutState = mock( SutState.class );
		when( sutState.getRootElements() ).thenReturn( Collections.singletonList( rootElement ) );

		final NoGoldenMasterActionReplayResult cut = new NoGoldenMasterActionReplayResult( "result", sutState, "path" );

		assertThat( cut.hasDifferences() ).isTrue();
	}
}
