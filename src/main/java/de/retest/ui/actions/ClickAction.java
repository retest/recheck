package de.retest.ui.actions;

import de.retest.ui.components.Clickable;
import de.retest.ui.components.Component;
import de.retest.ui.descriptors.Element;
import de.retest.ui.review.ActionChangeSet;

public class ClickAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public ClickAction( final Element element ) {
		super( element, null );
	}

	@Override
	public Action applyChanges( final ActionChangeSet reviewResult ) {
		throw new RuntimeException( "Generic action, not intended to persist!" );
	}

	@Override
	public String createDescription() {
		return "Click on " + element;
	}

	@Override
	public void execute( final Component<?> component ) throws TargetNotFoundException {
		if ( component instanceof Clickable ) {
			((Clickable) component).click();
			return;
		}
		throw new RuntimeException(
				"Don't know how to click a component of type " + component.getComponent().getClass().getName() + "!" );
	}

}
