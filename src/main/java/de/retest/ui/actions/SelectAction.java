package de.retest.ui.actions;

import de.retest.ui.components.Component;
import de.retest.ui.components.Selectable;
import de.retest.ui.descriptors.Element;
import de.retest.ui.review.ActionChangeSet;

public class SelectAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private final String text;

	public SelectAction( final Element element, final String text ) {
		super( element, null );
		this.text = text;
	}

	@Override
	public Action applyChanges( final ActionChangeSet reviewResult ) {
		throw new RuntimeException( "Generic action, not intended to persist!" );
	}

	@Override
	public String createDescription() {
		return "Select '" + text + "' on " + element;
	}

	@Override
	public void execute( final Component<?> component ) throws TargetNotFoundException {
		if ( component instanceof Selectable ) {
			((Selectable) component).selectByText( text );
			return;
		}
		throw new RuntimeException( "Don't know how to select with a component of type "
				+ component.getComponent().getClass().getName() + "!" );
	}

}
