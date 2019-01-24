package de.retest.ui;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import de.retest.remote.SelectionCallback;
import de.retest.remote.SelectionController;
import de.retest.ui.actions.Action;
import de.retest.ui.actions.ActionExecutionResult;
import de.retest.ui.actions.ExceptionWrapper;
import de.retest.ui.actions.TargetNotFoundException;
import de.retest.ui.components.Component;
import de.retest.ui.components.RootContainer;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.RootElement;
import de.retest.ui.image.Screenshot;

public abstract class Environment<T> implements DefaultValueFinder {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( Environment.class );

	protected CrashDetector crashDetector = new CrashDetector() {
		@Override
		public boolean hasCrashed() {
			return false;
		}

		@Override
		public ExceptionWrapper getCrashCause() {
			return null;
		}
	};

	public void setCrashDetector( final CrashDetector crashDetector ) {
		this.crashDetector = crashDetector;
	}

	public CrashDetector getCrashDetector() {
		return crashDetector;
	}

	public static interface Task {
		void execute();
	}

	/**
	 * Execute a complete {@link Task} (e.g. execution of a complete test) in a specially prepared environment (e.g. in
	 * a separate ApplicationContext).
	 *
	 * @param task
	 *            the task that has to be executed
	 */
	public abstract void execute( final Task task );

	/**
	 * Execute an individual {@link Action} in the environment (e.g. on the Swing GUI).
	 * <p>
	 * We must differentiate between an exception within the SUT and an exception in ReTest. An exception from the SUT
	 * is returned as execution result, an exception originating in retest is thrown like any ordinary exception.
	 *
	 * @param action
	 *            the action that has to be executed
	 *
	 * @return the result of the execution
	 *
	 */
	public abstract ActionExecutionResult execute( Action action );

	public abstract List<RootContainer<T>> getTargetableWindows();

	public abstract void waitForStabilization();

	public abstract void reset();

	public abstract boolean hasSystemExited();

	public abstract Screenshot getScreenshot( T component );

	public abstract Screenshot[] getWindowsScreenshots();

	public abstract Rectangle getOutlineInWindowCoordinates( T component );

	public abstract List<Action> getAllActions();

	public List<RootElement> getTargetableRootElements() {
		if ( hasSystemExited() ) {
			return new ArrayList<>();
		}
		/*
		 * Currently a state is only concerned with the windows it can actually interact with. It might make sense to
		 * add non-interactable windows for differentiating graph to out edges ambiguities. Also see the comment in
		 * RootElement.
		 */
		return getRootElements( getTargetableWindows() );
	}

	public ImmutablePair<TargetNotFoundException, Component<T>> findTargetComponent( final Element element ) {
		return TargetFinder.findTargetComponent( element, getTargetableWindows(), getWindowsScreenshots() );
	}

	public ImmutablePair<TargetNotFoundException, Component<T>> findTargetComponent( final Action action ) {
		return TargetFinder.findTargetComponent( action, getTargetableWindows(), getWindowsScreenshots() );
	}

	protected ArrayList<RootElement> getRootElements( final List<RootContainer<T>> windows ) {
		if ( hasSystemExited() ) {
			return new ArrayList<>();
		}
		final ArrayList<RootElement> result = new ArrayList<>();
		for ( final RootContainer<?> window : windows ) {
			result.add( window.getRootElement() );
		}
		return result;
	}

	public abstract void reloadWindows();

	protected boolean screenshots = true;

	public void disableScreenshots() {
		screenshots = false;
	}

	public void enableScreenshots() {
		screenshots = true;
	}

	public void handleReTestBug( final String msg, final Throwable exc ) {
		logger.error( "********************************************************************************" );
		logger.error( msg, exc );
		logger.error( "" );
		logger.error( "" );
		logger.error( "Congratulations, it appears you have found a bug in ReTest!" );
		logger.error( "Due to our constant effort to increase quality, this should be a rare event..." );
		logger.error( "" );
		logger.error(
				"And it is your chance! Your chance to improve ReTest. Your chance to give something back. Your chance to help us make this a better world." );
		logger.error(
				"Since you discovered this bug, you probably know already more about it than anybody else (i.e. how to trigger it)." );
		logger.error( "So please, if you can, analyze and fix the bug you just found." );
		logger.error( "Then go to https://github.com/retest/retest/wiki and find out how to contribute your fix." );
		logger.error( "" );
		logger.error( "Thank you. People like you are the reason this software is free." );
		logger.error( "********************************************************************************" );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDefaultValue( final IdentifyingAttributes comp, final String s,
			final Serializable attributesKey ) {
		return false;
	}

	public abstract SelectionController startComponentSelection( final SelectionCallback callback );

	/**
	 * Returns the execution trace in whatever format the current environment delivers / supports.
	 *
	 * Because we will have various formats and currently no common interface, this returns Object.
	 *
	 * TODO: Define common interface and adapters / wrappers for concrete implementations.
	 *
	 * @return the exectution trace
	 */
	public abstract Object getExecutionTrace();

}
