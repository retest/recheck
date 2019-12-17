package de.retest.recheck.ui.diff.meta;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class MetadataDifference implements Iterable<MetadataElementDifference>, Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	private final Set<MetadataElementDifference> differences;

	// For JAXB
	private MetadataDifference() {
		this( Collections.emptySet() );
	}

	public static MetadataDifference empty() {
		return new MetadataDifference( Collections.emptySet() );
	}

	public static MetadataDifference of( final Set<MetadataElementDifference> differences ) {
		final Set<MetadataElementDifference> unique =
				new TreeSet<>( Comparator.comparing( MetadataElementDifference::getKey ) );
		unique.addAll( differences );
		return new MetadataDifference( new LinkedHashSet<>( unique ) );
	}

	public boolean isEmpty() {
		return differences.isEmpty();
	}

	public int size() {
		return differences.size();
	}

	@Override
	public Iterator<MetadataElementDifference> iterator() {
		return differences.iterator();
	}

	@Override
	public Spliterator<MetadataElementDifference> spliterator() {
		return differences.spliterator();
	}

	@Override
	public void forEach( final Consumer<? super MetadataElementDifference> action ) {
		differences.forEach( action );
	}
}
