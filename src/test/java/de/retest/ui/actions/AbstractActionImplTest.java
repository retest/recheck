package de.retest.ui.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import de.retest.ui.components.Component;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.review.ActionChangeSet;

public class AbstractActionImplTest {

	@Test
	public void hashCode_should_return_same_value_for_different_objects() {
		final AbstractAction action1 = new ActionImpl();
		final AbstractAction action2 = new ActionImpl();
		Assertions.assertThat( action1.hashCode() ).isEqualTo( action2.hashCode() );
	}

	private static class ActionImpl extends AbstractAction {
		private static final Element element = mock( Element.class );

		public ActionImpl() {
			super( element, null );
			when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );
		}

		private static final long serialVersionUID = 1L;

		@Override
		public void execute( final Component<?> component ) {}

		@Override
		public Action applyChanges( final ActionChangeSet reviewResult ) {
			return null;
		}

		@Override
		public String createDescription() {
			return null;
		}
	}

	private static class Comp {}
}
