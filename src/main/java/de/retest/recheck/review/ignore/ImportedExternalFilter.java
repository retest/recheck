package de.retest.recheck.review.ignore;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.ImportExternalFilterLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class ImportedExternalFilter implements Filter {

	private final String reference;
	private final Filter loaded;

	public ImportedExternalFilter( final String reference, final Filter loaded ) {
		this.loaded = loaded;
		this.reference = reference;
	}

	@Override
	public boolean matches( final Element element ) {
		return loaded.matches( element );
	}

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		return loaded.matches( element, attributeKey );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return loaded.matches( element, attributeDifference );
	}

	@Override
	public String toString() {
		return String.format( ImportExternalFilterLoader.FORMAT, reference );
	}

	public String getReference() {
		return reference;
	}

	public Filter getReferenced() {
		return loaded;
	}
}
