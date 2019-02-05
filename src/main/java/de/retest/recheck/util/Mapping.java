package de.retest.recheck.util;

import java.io.Serializable;
import java.util.Map;

/**
 * Implements a tuple or mapping. Commonly used in various places...
 *
 * @author roessler
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class Mapping<K, V> implements Map.Entry<K, V>, Serializable {
	private static final long serialVersionUID = 1L;

	private final K k;
	private V v;

	public Mapping( final K k, final V v ) {
		this.k = k;
		this.v = v;
	}

	@Override
	public K getKey() {
		return k;
	}

	@Override
	public V getValue() {
		return v;
	}

	@Override
	public V setValue( final V value ) {
		final V result = v;
		v = value;
		return result;
	}

	@Override
	public int hashCode() {
		return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj != null && getClass() == obj.getClass() ) {
			final Mapping<?, ?> other = (Mapping<?, ?>) obj;
			if ( k != null && k.equals( other.k ) && v != null && v.equals( other.v ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return k + ":" + v;
	}
}
