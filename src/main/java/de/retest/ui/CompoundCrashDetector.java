package de.retest.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.retest.ui.actions.ExceptionWrapper;

public class CompoundCrashDetector implements CrashDetector {

	private final List<CrashDetector> detectors = new ArrayList<>();

	public CompoundCrashDetector() {}

	public CompoundCrashDetector( final CrashDetector... crashDetectors ) {
		detectors.addAll( Arrays.asList( crashDetectors ) );
	}

	public void addCrashDetector( final CrashDetector detector ) {
		detectors.add( detector );
	}

	@Override
	public boolean hasCrashed() {
		for ( final CrashDetector crashDetector : detectors ) {
			if ( crashDetector.hasCrashed() ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ExceptionWrapper getCrashCause() {
		for ( final CrashDetector crashDetector : detectors ) {
			final ExceptionWrapper result = crashDetector.getCrashCause();
			if ( result != null ) {
				return result;
			}
		}
		return null;
	}

}
