package de.retest.recheck.util.junit.vintage;

import java.util.Locale;

import org.junit.rules.ExternalResource;

public class LocaleRule extends ExternalResource {

	private static final Locale defaultLocale = Locale.getDefault();

	private final Locale tempLocale;

	public LocaleRule( final Locale tempLocale ) {
		this.tempLocale = tempLocale;
	}

	@Override
	protected void before() throws Throwable {
		Locale.setDefault( tempLocale );
	}

	@Override
	protected void after() {
		Locale.setDefault( defaultLocale );
	}

}
