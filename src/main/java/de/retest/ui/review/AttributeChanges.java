package de.retest.ui.review;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.retest.ui.Path;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.diff.AttributeDifference;

public class AttributeChanges {

	AttributeChanges() {}

	private final Map<Path, Set<AttributeDifference>> changes = new HashMap<>();

	public boolean contains( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		final Path path = identifyingAttributes.getPathTyped();
		return changes.containsKey( path ) && changes.get( path ).contains( attributeDifference );
	}

	public void add( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		final Path path = identifyingAttributes.getPathTyped();
		if ( changes.containsKey( path ) ) {
			changes.get( path ).add( attributeDifference );
		} else {
			final Set<AttributeDifference> differences = new HashSet<>();
			differences.add( attributeDifference );
			changes.put( path, differences );
		}
	}

	public void remove( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		final Set<AttributeDifference> attributeDifferences = changes.get( identifyingAttributes.getPathTyped() );
		if ( attributeDifferences != null ) {
			attributeDifferences.remove( attributeDifference );
		}
	}

	public Set<AttributeDifference> getAll( final IdentifyingAttributes identifyingAttributes ) {
		final Set<AttributeDifference> result = changes.get( identifyingAttributes.getPathTyped() );
		if ( result != null ) {
			return result;
		} else {
			return Collections.emptySet();
		}
	}

	public boolean isEmpty() {
		return changes.isEmpty();
	}

	public void addAll( final IdentifyingAttributes identifyingAttributes,
			final List<AttributeDifference> attributes ) {
		for ( final AttributeDifference attributeDifference : attributes ) {
			add( identifyingAttributes, attributeDifference );
		}
	}

	@Override
	public String toString() {
		return "AttributeChanges [" + changes + "]";
	}

	public int size() {
		return changes.size();
	}
}
