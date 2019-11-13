package de.retest.recheck.review.ignore.io;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexLoader<T> implements Loader<T> {

	private final Pattern regex;

	public RegexLoader( final Pattern regex ) {
		this.regex = regex;
	}

	@Override
	public Optional<T> load( final String line ) {
		final Matcher matcher = regex.matcher( line );
		if ( !matcher.matches() ) {
			return Optional.empty();
		}
		return load( matcher.toMatchResult() );
	}

	protected abstract Optional<T> load( final MatchResult matcher );

	@Override
	public String save( final T ignore ) {
		return ignore.toString();
	}
}
