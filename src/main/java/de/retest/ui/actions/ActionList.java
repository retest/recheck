package de.retest.ui.actions;

import java.util.EventObject;

public interface ActionList {

	void addAction( Action action, EventObject eo );

	Action getLastAction();

	public abstract class ActionListStub implements ActionList {
		private Action last;

		@Override
		public void addAction( final Action action, final EventObject eo ) {
			last = action;
			addAction( action );
		}

		public abstract void addAction( Action action );

		@Override
		public Action getLastAction() {
			return last;
		}
	}

}
