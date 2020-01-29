package de.retest.recheck.review.ignore.io;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.SearchFilterFiles;
import de.retest.recheck.review.ignore.ImportedExternalFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportExternalFilterLoader extends RegexLoader<ImportedExternalFilter> {

	public static final String IMPORT_STATEMENT = "import: ";
	public static final String FORMAT = IMPORT_STATEMENT + "%s";
	private static final Pattern PREFIX = Pattern.compile( IMPORT_STATEMENT + "(.+)" );

	public ImportExternalFilterLoader() {
		super( PREFIX );
	}

	@Override
	protected Optional<ImportedExternalFilter> load( final MatchResult matcher ) {
		final String reference = matcher.group( 1 );
		try {
			final Filter loaded = SearchFilterFiles.getFilterByName( reference );
			return Optional.of( new ImportedExternalFilter( reference, loaded ) );
		} catch ( final Exception e ) {
			log.error( "Exception loading referenced filter '{}'.", reference );
			return Optional.empty();
		} catch ( final StackOverflowError e ) {
			log.error( "Encountered an infinite loop when loading cascading filters, e.g. see '{}'.", reference );
			return Optional.empty();
		}
	}
}
