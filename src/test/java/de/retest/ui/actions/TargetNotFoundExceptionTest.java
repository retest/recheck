package de.retest.ui.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.Mockito;

import de.retest.ui.descriptors.Element;

public class TargetNotFoundExceptionTest {

	@Test
	public void getMissingTarget_returns_the_target_invocation_descriptor() {
		final Action action = mock( Action.class );
		final Element result = mock( Element.class );
		Mockito.when( action.getTargetElement() ).thenReturn( result );
		assertThat( new TargetNotFoundException( action, null, null, null ).getMissingTarget() ).isSameAs( result );
	}
}
