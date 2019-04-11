package de.retest.recheck.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.Difference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.review.ActionChangeSet;

public class AcceptableElementDifference {

	private static final Filter SHOULD_IGNORE_NOTHING = null;

	private final ElementDifference elementDifference;
	private final List<AttributeDifference> identifyingAttributesAttributesDifferences;
	private final List<AttributeDifference> attributeDifferences;
	private final ActionChangeSet acceptedChanges;

	private final GlobalChangeSetApplier globalChangeApplier;
	private final GlobalIgnoreApplier globalIgnoreApplier;

	public AcceptableElementDifference( final ElementDifference elementDifference,
			final ActionChangeSet acceptedChanges, final GlobalChangeSetApplier globalChangeApplier,
			final GlobalIgnoreApplier globalIgnoreApplier ) {
		this.elementDifference = elementDifference;
		this.globalChangeApplier = globalChangeApplier;
		this.globalIgnoreApplier = globalIgnoreApplier;
		this.acceptedChanges = acceptedChanges;

		final List<AttributeDifference> prepareIdentifyingAttributesDifferences = new ArrayList<>();
		final List<AttributeDifference> prepareAttributeDifferences = new ArrayList<>();
		for ( final Difference difference : elementDifference.getImmediateDifferences() ) {
			if ( difference instanceof IdentifyingAttributesDifference ) {
				prepareIdentifyingAttributesDifferences.addAll(
						((IdentifyingAttributesDifference) difference).getAttributeDifferences() );
			}
			if ( difference instanceof AttributesDifference ) {
				prepareAttributeDifferences.addAll( ((AttributesDifference) difference).getDifferences() );
			}
		}

		identifyingAttributesAttributesDifferences = Collections.unmodifiableList(
				prepareIdentifyingAttributesDifferences );
		attributeDifferences = Collections.unmodifiableList( prepareAttributeDifferences );
	}

	public ElementDifference getElementDifference() {
		return elementDifference;
	}

	public List<AttributeDifference> getAttributeDifferences() {
		final List<AttributeDifference> result = new ArrayList<>();
		result.addAll( identifyingAttributesAttributesDifferences );
		result.addAll( attributeDifferences );
		return result;
	}

	public boolean isAccepted() {
		if ( globalIgnoreApplier.shouldBeFiltered( getElementDifference().getElement() ) ) {
			return true;
		}
		boolean result = true;
		for ( final AttributeDifference attributeDifference : getAttributeDifferences() ) {
			result &= isAccepted( attributeDifference ) || isIgnored( attributeDifference );
		}
		return result;
	}

	public boolean isAccepted( final AttributeDifference attributeDifference ) {
		if ( isIdentAttribute( attributeDifference ) ) {
			return acceptedChanges.getIdentAttributeChanges().contains( elementDifference.getIdentifyingAttributes(),
					attributeDifference );
		} else {
			return acceptedChanges.getAttributesChanges().contains( elementDifference.getIdentifyingAttributes(),
					attributeDifference );
		}
	}

	public void accept( final AttributeDifference attributeDifference ) {
		final IdentifyingAttributes identifyingAttributes = elementDifference.getIdentifyingAttributes();
		assert elementDifference.getAttributeDifferences( SHOULD_IGNORE_NOTHING ).contains( attributeDifference );
		if ( elementDifference.isInsertion() ) {
			globalChangeApplier.addChangeSetForAllEqualInsertedChanges(
					((InsertedDeletedElementDifference) elementDifference.getIdentifyingAttributesDifference())
							.getActual() );
		} else if ( elementDifference.isDeletion() ) {
			globalChangeApplier.addChangeSetForAllEqualDeletedChanges( identifyingAttributes );
		} else if ( isIdentAttribute( attributeDifference ) ) {
			globalChangeApplier.addChangeSetForAllEqualIdentAttributeChanges( identifyingAttributes,
					attributeDifference );
		} else {
			globalChangeApplier.createChangeSetForAllEqualAttributesChanges( identifyingAttributes,
					attributeDifference );
		}
	}

	public void unaccept( final AttributeDifference attributeDifference ) {
		final IdentifyingAttributes identifyingAttributes = elementDifference.getIdentifyingAttributes();
		if ( elementDifference.isInsertion() ) {
			globalChangeApplier.removeChangeSetForAllEqualInsertedChanges(
					((InsertedDeletedElementDifference) elementDifference.getIdentifyingAttributesDifference())
							.getActual() );
		} else if ( elementDifference.isDeletion() ) {
			globalChangeApplier.removeChangeSetForAllEqualDeletedChanges( identifyingAttributes );
		} else if ( isIdentAttribute( attributeDifference ) ) {
			globalChangeApplier.removeChangeSetForAllEqualIdentAttributeChanges( identifyingAttributes,
					attributeDifference );
		} else {
			globalChangeApplier.removeChangeSetForAllEqualAttributesChanges( identifyingAttributes,
					attributeDifference );
		}
	}

	private boolean isIdentAttribute( final AttributeDifference attributeDifference ) {
		final boolean identifyingAttributesAttrib = identifyingAttributesAttributesDifferences.contains(
				attributeDifference );
		final boolean attributesAttrib = attributeDifferences.contains( attributeDifference );

		assert identifyingAttributesAttrib != attributesAttrib : "attribute must exist in exact one of both lists";

		return identifyingAttributesAttrib;
	}

	public boolean isIgnored( final AttributeDifference difference ) {
		return globalIgnoreApplier.shouldBeFiltered( getElementDifference().getElement(), difference );
	}

	public void ignore( final AttributeDifference attributeDifference ) {
		globalIgnoreApplier.ignoreAttribute( getElementDifference().getElement(), attributeDifference );
	}

	public void unignore( final AttributeDifference attributeDifference ) {
		globalIgnoreApplier.unignoreAttribute( getElementDifference().getElement(), attributeDifference );
	}
}
