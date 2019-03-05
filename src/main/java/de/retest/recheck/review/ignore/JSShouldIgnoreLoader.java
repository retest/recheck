package de.retest.recheck.review.ignore;

import de.retest.recheck.ignore.JSShouldIgnoreImpl;
import de.retest.recheck.review.ignore.io.Loader;

public class JSShouldIgnoreLoader implements Loader<JSShouldIgnoreImpl> {

	@Override
	public JSShouldIgnoreImpl load( final String line ) {
		return new JSShouldIgnoreImpl( null );
	}

	@Override
	public String save( final JSShouldIgnoreImpl ignore ) {
		return "";
	}
}
