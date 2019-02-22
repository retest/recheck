package de.retest.recheck.review.ignore.matcher;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementRetestIdMatcher implements Matcher<Element> {

	private final String retestid;

	public ElementRetestIdMatcher( final Element element ) {
		this( element.getRetestId() );
	}

	private ElementRetestIdMatcher( final String id ) {
		retestid = id;
	}

	@Override
	public boolean test( final Element element ) {
		return element.getRetestId().matches( retestid );
	}

	@Override
	public String toString() {
		return String.format( ElementRetestIdMatcherLoader.FORMAT, retestid );
	}

	public static final class ElementRetestIdMatcherLoader extends RegexLoader<ElementRetestIdMatcher> {

		private static final String RETEST_ID = "retestid=";

		private static final String FORMAT = RETEST_ID + "%s";
		private static final Pattern REGEX = Pattern.compile( RETEST_ID + "(.+)" );

		public ElementRetestIdMatcherLoader() {
			super( REGEX );
		}

		@Override
		protected ElementRetestIdMatcher load( final MatchResult matcher ) {
			final String id = matcher.group( 1 );
			return new ElementRetestIdMatcher( id );
		}

		@Override
		public String save( final ElementRetestIdMatcher ignore ) {
			return ignore.toString();
		}
	}
}
