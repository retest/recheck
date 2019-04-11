package de.retest.recheck.ignore;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

/**
 * @deprecated As of release 1.1.0, replaced by {@link de.retest.recheck.ignore.Filter}
 */
@Deprecated
public interface ShouldIgnore {

	/**
	 * @deprecated As of release 1.1.0, replaced by {@link de.retest.recheck.ignore.Filter#shouldBeFiltered(Element)}
	 */
	@Deprecated
	boolean shouldIgnoreElement( final Element element );

	/**
	 * @deprecated As of release 1.1.0, replaced by
	 *             {@link de.retest.recheck.ignore.Filter#shouldBeFiltered(Element, AttributeDifference)}
	 */
	@Deprecated
	boolean shouldIgnoreAttributeDifference( final Element element, AttributeDifference attributeDifference );

	public static final ShouldIgnore IGNORE_NOTHING = new ShouldIgnore() {

		@Override
		public boolean shouldIgnoreElement( final Element element ) {
			return false;
		}

		@Override
		public boolean shouldIgnoreAttributeDifference( final Element element,
				final AttributeDifference attributeDifference ) {
			return false;
		}
	};
}
