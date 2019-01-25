package de.retest.ui;

import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;

/**
 * {@code RecheckIgnore} are user-specific and may change during runtime, whereas {@code IgnoredTypes} are
 * toolkit-specific and known at compile time.
 */
public interface IgnoredTypes {

	public boolean contains( final Element element );

	public boolean contains( final IdentifyingAttributes identifyingAttributes );

	public static class NoIgnoredTypes implements IgnoredTypes {
		@Override
		public boolean contains( final Element element ) {
			return false;
		}

		@Override
		public boolean contains( final IdentifyingAttributes identifyingAttributes ) {
			return false;
		}

	}

}
