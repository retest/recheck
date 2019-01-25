package de.retest.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements a {@link Set} but does neither use a native order of elements (as would be implemented with
 * {@link Comparator} or {@link Comparable} and a {@link TreeSet}) nor does it depend on the {@link Object#hashCode()}
 * method. See <a href=
 * "http://stackoverflow.com/questions/26971127/nullpointerexception-in-hashcode-when-deserializing-an-object-graph/26971732#26971732"
 * >this question on stackoverflow</a> for details as to why using HashMap and HashSet is a problem.
 *
 * The resulting code operates in O(n) where n is the number of elements in this data structure. Therefore this is only
 * suitable for small amounts of elements.
 *
 * @author roessler
 *
 * @see ListMap
 *
 * @param <E>
 *            The type elements of this set.
 */
public class ListSet<E> extends AbstractSet<E> implements Set<E>, java.io.Serializable {
	static final long serialVersionUID = 1L;

	private final List<E> values;

	ListSet( final ArrayList<E> list ) {
		values = list;
	}

	public ListSet() {
		values = new ArrayList<>();
	}

	public ListSet( final Collection<? extends E> c ) {
		values = new ArrayList<>();
		addAll( c );
	}

	@Override
	public Iterator<E> iterator() {
		return values.iterator();
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean contains( final Object o ) {
		return values.contains( o );
	}

	@Override
	public boolean add( final E e1 ) {
		for ( int idx = 0; idx < values.size(); idx++ ) {
			final E e2 = values.get( idx );
			if ( e1 == null && e2 == null || e2 != null && e2.equals( e1 ) ) {
				values.set( idx, e1 );
				return false;
			}
		}
		values.add( e1 );
		return true;
	}

	@Override
	public boolean remove( final Object e1 ) {
		for ( int idx = 0; idx < values.size(); idx++ ) {
			final E e2 = values.get( idx );
			if ( e1 == null && e2 == null || e2 != null && e2.equals( e1 ) ) {
				values.remove( idx );
				return true;
			}
		}
		return false;
	}

	@Override
	public void clear() {
		values.clear();
	}
}
