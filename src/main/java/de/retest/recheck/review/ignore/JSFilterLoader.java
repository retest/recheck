package de.retest.recheck.review.ignore;

import java.util.Optional;

import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.review.ignore.io.Loader;

public class JSFilterLoader implements Loader<JSFilterImpl> {

	@Override
	public Optional<JSFilterImpl> load( final String line ) {
		// XXX This causes exceptions when something is loaded which other loaders can't load.
		return Optional.empty();
	}

	@Override
	public String save( final JSFilterImpl ignore ) {
		return "";
	}
}
