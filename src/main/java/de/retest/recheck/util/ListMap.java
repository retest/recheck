package de.retest.recheck.util;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

/**
 * This class implements a {@link Map} but does neither use a native order of elements (as would be implemented with
 * {@link Comparator} or {@link Comparable} and a {@link TreeMap}) nor does it depend on the {@link Object#hashCode()}
 * method. See <a href=
 * "http://stackoverflow.com/questions/26971127/nullpointerexception-in-hashcode-when-deserializing-an-object-graph/26971732#26971732"
 * >this question on stackoverflow</a> for details as to why using HashMap and HashSet is a problem.
 *
 * The resulting code operates in O(n) where n is the number of elements in this data structure. Therefore this is only
 * suitable for small amounts of elements.
 *
 * @author roessler
 *
 * @see ListSet
 *
 * @param <K>
 *            The type of the keys of this map.
 * @param <V>
 *            They type of the values of this map.
 */
public class ListMap<K, V> extends AbstractMap<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ArrayList<Pair<K, V>> mappings = new ArrayList<>();

	public ListMap() {}

	public ListMap( final Map<? extends K, ? extends V> otherMap ) {
		putAll( otherMap );
	}

	@Override
	public int size() {
		return mappings.size();
	}

	@Override
	public boolean isEmpty() {
		return mappings.isEmpty();
	}

	@Override
	public boolean containsKey( final Object k1 ) {
		for ( final Pair<K, V> mapping : mappings ) {
			final K k2 = mapping.getKey();
			if ( k1 == null && k2 == null || k2 != null && k2.equals( k1 ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue( final Object v1 ) {
		for ( final Pair<K, V> mapping : mappings ) {
			final V v2 = mapping.getValue();
			if ( v1 == null && v2 == null || v2 != null && v2.equals( v1 ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public V get( final Object k1 ) {
		for ( final Pair<K, V> mapping : mappings ) {
			final K k2 = mapping.getKey();
			if ( k1 == null && k2 == null || k2 != null && k2.equals( k1 ) ) {
				return mapping.getValue();
			}
		}
		return null;
	}

	@Override
	public V put( final K k1, final V value ) {
		for ( final Pair<K, V> mapping : mappings ) {
			final K k2 = mapping.getKey();
			if ( k1 == null && k2 == null || k2 != null && k2.equals( k1 ) ) {
				final V result = mapping.getValue();
				mapping.setValue( value );
				return result;
			}
		}
		mappings.add( Pair.of( k1, value ) );
		return null;
	}

	@Override
	public V remove( final Object k1 ) {
		for ( int idx = 0; idx < mappings.size(); idx++ ) {
			final Pair<K, V> mapping = mappings.get( idx );
			final K k2 = mapping.getKey();
			if ( k1 == null && k2 == null || k2 != null && k2.equals( k1 ) ) {
				mappings.remove( idx );
				return mapping.getValue();
			}
		}
		return null;
	}

	@Override
	public void putAll( final Map<? extends K, ? extends V> m ) {
		for ( final Map.Entry<? extends K, ? extends V> entry : m.entrySet() ) {
			put( entry.getKey(), entry.getValue() );
		}
	}

	@Override
	public void clear() {
		mappings.clear();
	}

	@Override
	public Set<K> keySet() {
		final ListSet<K> result = new ListSet<>();
		for ( final Pair<K, V> mapping : mappings ) {
			result.add( mapping.getKey() );
		}
		return result;
	}

	@Override
	public Collection<V> values() {
		final ArrayList<V> result = new ArrayList<>();
		for ( final Pair<K, V> mapping : mappings ) {
			result.add( mapping.getValue() );
		}
		return result;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		final Set<Map.Entry<K, V>> result = new ListSet<>();
		for ( final Pair<K, V> mapping : mappings ) {
			result.add( Pair.of( mapping.getKey(), mapping.getValue() ) );
		}
		return result;
	}
}
