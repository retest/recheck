package de.retest.recheck.review.ignore;

import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.review.ignore.io.Loader;

public class JSShouldIgnoreLoader implements Loader<JSFilterImpl> {

	@Override
	public JSFilterImpl load( final String line ) {
		return new JSFilterImpl( null );
	}

	@Override
	public String save( final JSFilterImpl ignore ) {
		return "";
	}
}
