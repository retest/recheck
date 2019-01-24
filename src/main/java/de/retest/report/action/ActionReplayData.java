package de.retest.report.action;

import de.retest.ui.actions.Action;
import de.retest.ui.descriptors.Element;

public class ActionReplayData {

	private final String description;
	private final Element element;
	private final String stateFilePath;

	private ActionReplayData( final String description, final Element element, final String stateFilePath ) {
		this.description = description;
		this.element = element;
		this.stateFilePath = stateFilePath;
	}

	public static ActionReplayData of( final Action action ) {
		if ( action != null ) {
			return withTarget( action.toString(), action.getTargetElement() );
		} else {
			return ofSutStart();
		}
	}

	public static ActionReplayData ofSutStart() {
		return withoutTarget( "Start Sut" );
	}

	public static ActionReplayData empty() {
		return new ActionReplayData( null, null, null );
	}

	public static ActionReplayData withTarget( final String description, final Element element ) {
		return withTarget( description, element, null );
	}

	public static ActionReplayData withTarget( final String description, final Element element,
			final String stateFilePath ) {
		return new ActionReplayData( description, element, stateFilePath );
	}

	public static ActionReplayData withoutTarget( final String description ) {
		return withoutTarget( description, null );
	}

	public static ActionReplayData withoutTarget( final String description, final String stateFilePath ) {
		return new ActionReplayData( description, null, stateFilePath );
	}

	public String getDescription() {
		return description;
	}

	public Element getElement() {
		return element;
	}

	public String getStateFilePath() {
		return stateFilePath;
	}
}
