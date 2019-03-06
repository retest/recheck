package de.retest.recheck.review.ignore.io;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexLoader<T> implements Loader<T> {

	private final Pattern regex;

	public RegexLoader( final Pattern regex ) {
		this.regex = regex;
	}

	@Override
	public boolean canLoad( final String line ) {
		return regex.matcher( line ).matches();
	}

	@Override
	public T load( final String line ) {
		final Matcher matcher = regex.matcher( line );
		if ( !matcher.find() ) {
			throw new IllegalArgumentException( "The line '" + line + "' does not match '" + regex.pattern() + "'." );
		}
		return load( matcher.toMatchResult() );
	}

	protected abstract T load( final MatchResult matcher );

	@Override
	public String save( final T ignore ) {
		return ignore.toString();
	}
}
