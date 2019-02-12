package de.retest.recheck.ui.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.components.Component;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.review.ActionChangeSet;

class AbstractActionImplTest {

	@Test
	void hashCode_should_return_same_value_for_different_objects() {
		final AbstractAction action1 = new ActionImpl();
		final AbstractAction action2 = new ActionImpl();
		assertThat( action1 ).hasSameHashCodeAs( action2 );
	}

	static class ActionImpl extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private static final Element element = mock( Element.class );

		public ActionImpl() {
			super( element, null );
			when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );
		}

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

}
