package de.retest.recheck.review.ignore;

import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;

/**
 * Use {@link MatcherFilter} instead.
 */
// TODO Remove for a 2.0 release
@Deprecated
public class ElementFilter extends MatcherFilter {

	public ElementFilter( final Matcher<Element> matcher ) {
		super( matcher );
	}

}
