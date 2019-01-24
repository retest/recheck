package de.retest.ui.diff;

import static de.retest.util.ObjectUtil.compare;
import static de.retest.util.ObjectUtil.isNullOrEmptyString;
import static de.retest.util.ObjectUtil.nextHashCode;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.ui.descriptors.Attribute;
import de.retest.util.ChecksumCalculator;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class AttributeDifference implements Comparable<AttributeDifference>, Serializable {

	private static final Logger logger = LoggerFactory.getLogger( AttributeDifference.class );

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String key;

	// we call this attributeId instead of attribute,
	// because we XSL-transform the resulting XML
	// and XSL is not type-safe
	@XmlAttribute
	private final String attributeDifferenceId;

	// JAXB has a problem if these are set to Serializable:
	@XmlElement
	private final Object expected;
	@XmlElement
	private final Object actual;

	protected AttributeDifference() {
		// for JAXB
		key = null;
		expected = null;
		actual = null;
		attributeDifferenceId = null;
	}

	public AttributeDifference( final String key, final Serializable expected, final Serializable actual ) {
		this.key = key;
		this.expected = expected;
		this.actual = actual;
		attributeDifferenceId = identifier();
	}

	public String getKey() {
		return key;
	}

	public Serializable getExpected() {
		return (Serializable) expected;
	}

	public Serializable getActual() {
		return (Serializable) actual;
	}

	public String identifier() {
		final String contents = Arrays.asList( actual, expected ).stream() //
				.filter( Objects::nonNull ) //
				.map( Object::toString ) //
				.collect( Collectors.joining( " # " ) );

		return ChecksumCalculator.getInstance().sha256( contents );
	}

	public Attribute applyChangeTo( final Attribute attribute ) {
		if ( attribute == null ) {
			throw new NullPointerException( "Cannot apply change to an attribute that is null." );
		}
		warnIfAttributesDontMatch( attribute.getValue() );
		return attribute.applyChanges( getActual() );
	}

	protected final void warnIfAttributesDontMatch( final Serializable fromAttribute ) {
		final Serializable expected = getExpected();
		if ( isNullOrEmptyString( fromAttribute ) ) {
			if ( isNullOrEmptyString( expected ) ) {
				return;
			}
		} else if ( expected != null && fromAttribute.toString().equals( expected.toString() ) ) {
			return;
		}
		logger.warn(
				"Mismatch for attribute '{}': value from ExecSuite '{}', value from TestResult '{}'. This could be due to a change of the execsuite in between.",
				key, fromAttribute, expected );
	}

	@Override
	public int hashCode() {
		return nextHashCode( nextHashCode( nextHashCode( 1, (Serializable) actual ), (Serializable) expected ), key );
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof AttributeDifference) ) {
			return false;
		}
		final AttributeDifference other = (AttributeDifference) obj;
		return Objects.equals( key, other.key ) && Objects.equals( expected, other.expected )
				&& Objects.equals( actual, other.actual );
	}

	@Override
	public int compareTo( final AttributeDifference other ) {
		if ( other == null ) {
			return 1;
		}
		int result = compare( 0, key, other.getKey() );
		result = compare( result, (Serializable) expected, other.getExpected() );
		return compare( result, (Serializable) actual, other.getActual() );
	}

	@Override
	public String toString() {
		return key + ": expected=\"" + expected + "\", actual=\"" + actual + "\"";
	}

	public static String getSumIdentifier( final List<AttributeDifference> attributeDifferences ) {
		final StringBuilder result = new StringBuilder();
		for ( final AttributeDifference attributeDifference : attributeDifferences ) {
			result.append( " # " ).append( attributeDifference.identifier() );
		}
		return ChecksumCalculator.getInstance().sha256( result.toString() );
	}
}
