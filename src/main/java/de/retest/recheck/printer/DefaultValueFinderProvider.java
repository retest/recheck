package de.retest.recheck.printer;

import de.retest.recheck.ui.DefaultValueFinder;

@FunctionalInterface
public interface DefaultValueFinderProvider {

	DefaultValueFinder findForAction( String name );

	static DefaultValueFinderProvider none() {
		final DefaultValueFinder noDefaults = ( attributes, key, value ) -> false;
		return name -> noDefaults;
	}
}
