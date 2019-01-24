package de.retest.ui;

import de.retest.ui.actions.ExceptionWrapper;

public interface CrashDetector {

	boolean hasCrashed();

	ExceptionWrapper getCrashCause();
}
