package de.retest.recheck.ignore;

import java.util.List;

import de.retest.recheck.ui.diff.AttributeDifference;

/**
 * Focus on individual ShouldIgnores. This allows to have a global CompoundShouldIgnore, that combines all individual
 * ones and then in the GUI filter the view and remove all ignored. But it allows also the opposite: per ShouldIgnore
 * (e.g. as loaded from file) show all elements and diffs from the current report that are ignored, sorted by individual
 * ShouldIgnores (e.g. recheck.ignore, recheck.ignore.js, ...).
 */
public class ShouldIgnoreUtil {

	public static List<AttributeDifference> removeIgnored( final ShouldIgnore ignore,
			final List<AttributeDifference> attributeDifferences ) {
		// TODO Implement
		return attributeDifferences;
	}

}
