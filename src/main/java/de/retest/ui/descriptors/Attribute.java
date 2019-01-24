package de.retest.ui.descriptors;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class Attribute implements Serializable, Comparable<Attribute> {

	private static final long serialVersionUID = 1L;

	public static final double IGNORE_WEIGHT = 0.0d;
	public static final double NORMAL_WEIGHT = 1.0d;

	public static final double FULL_MATCH = 1.0d;
	public static final double NO_MATCH = 0.0d;

	public static final int COMPARE_BIGGER = 1;
	public static final int COMPARE_EQUAL = 0;
	public static final int COMPARE_SMALLER = -1;

	@XmlAttribute
	private final String key;

	// Used by JaxB
	protected Attribute() {
		key = null;
	}

	protected Attribute( final String key ) {
		assert key != null : "Key may not be null!";
		this.key = key;
	}

	public final String getKey() {
		return key;
	}

	@Override
	public final int hashCode() {
		return key.hashCode() + 31 * (getValue() == null ? 0 : getValue().hashCode());
	}

	@Override
	public final boolean equals( final Object obj ) {
		if ( obj == null ) {
			return false;
		}
		if ( this == obj ) {
			return true;
		}
		if ( !(obj instanceof Attribute) ) {
			return false;
		}
		final Attribute other = (Attribute) obj;
		if ( !key.equals( other.key ) ) {
			return false;
		}
		if ( getValue() == null ) {
			if ( other.getValue() != null ) {
				return false;
			}
		} else if ( !getValue().equals( other.getValue() ) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Attribute [key=" + key + ", value=" + getValue() + "]";
	}

	/**
	 * Beware that JAXB does not differentiate between empty string and {@code null}.
	 *
	 * @return attribute value
	 */
	public abstract Serializable getValue();

	public abstract double match( final Attribute other );

	public abstract Attribute applyChanges( Serializable actual );

	public double getWeight() {
		return NORMAL_WEIGHT;
	}

	/**
	 * @return {@code true} if the attribute is meaningful to the user, i.e. can be visually detected by the user when
	 *         examining the SUT. Attributes that are invisible (e.g. HTML name and id) should return {@code false}.
	 */
	public boolean isVisible() {
		return true;
	}

	public final boolean isNotVisible() {
		return !isVisible();
	}

	@Override
	public int compareTo( final Attribute other ) {
		if ( other == null ) {
			return COMPARE_BIGGER;
		}
		if ( !getKey().equals( other.getKey() ) ) {
			return getKey().compareTo( other.getKey() );
		}
		if ( getValue() == null ) {
			if ( other.getValue() == null ) {
				return COMPARE_EQUAL;
			}
			return COMPARE_SMALLER;
		}
		if ( other.getValue() == null ) {
			return COMPARE_BIGGER;
		}
		return getValue().toString().compareTo( other.getValue().toString() );
	}
}
