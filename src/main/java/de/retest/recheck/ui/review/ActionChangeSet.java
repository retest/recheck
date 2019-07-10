package de.retest.recheck.ui.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

public class ActionChangeSet {

	private final String description;
	private final String goldenMasterPath;

	public ActionChangeSet() {
		this( null, null );
	}

	public ActionChangeSet( final String description, final String goldenMasterPath ) {
		this.description = description;
		this.goldenMasterPath = goldenMasterPath;
	}

	private final AttributeChanges identAttributeChanges = new AttributeChanges();

	public AttributeChanges getIdentAttributeChanges() {
		return identAttributeChanges;
	}

	private final AttributeChanges attributeChanges = new AttributeChanges();

	public AttributeChanges getAttributesChanges() {
		return attributeChanges;
	}

	public List<AttributeChanges> getAllAttributeChanges() {
		final List<AttributeChanges> allAttributeChanges = new ArrayList<>();
		allAttributeChanges.add( attributeChanges );
		allAttributeChanges.add( identAttributeChanges );
		return allAttributeChanges;
	}

	private final Set<Element> insertChanges = new HashSet<>();

	public boolean containsInsertChange( final Element element ) {
		return insertChanges.contains( element );
	}

	public void addInsertChange( final Element element ) {
		insertChanges.add( element );
	}

	public void removeInsertChange( final Element element ) {
		insertChanges.remove( element );
	}

	public Set<Element> getInsertedChanges() {
		return Collections.unmodifiableSet( insertChanges );
	}

	private final Set<IdentifyingAttributes> deletedChanges = new HashSet<>();

	public boolean containsDeletedChange( final IdentifyingAttributes identifyingAttributes ) {
		return deletedChanges.contains( identifyingAttributes );
	}

	public void addDeletedChange( final IdentifyingAttributes identifyingAttributes ) {
		deletedChanges.add( identifyingAttributes );
	}

	public void removeDeletedChange( final IdentifyingAttributes identifyingAttributes ) {
		deletedChanges.remove( identifyingAttributes );
	}

	public Set<IdentifyingAttributes> getDeletedChanges() {
		return Collections.unmodifiableSet( deletedChanges );
	}

	public boolean isEmpty() {
		return identAttributeChanges.isEmpty() && attributeChanges.isEmpty() && insertChanges.isEmpty()
				&& deletedChanges.isEmpty();
	}

	private int size() {
		return identAttributeChanges.size() + attributeChanges.size() + insertChanges
				.size() + deletedChanges.size();
	}

	@Override
	public String toString() {
		return "ActionChangeSet [" + size() + " changes]";
	}

	public String getDescription() {
		return description;
	}

	public String getGoldenMasterPath() {
		return goldenMasterPath;
	}
}
