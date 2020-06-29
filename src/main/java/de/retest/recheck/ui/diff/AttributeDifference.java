package de.retest.recheck.ui.diff;

import static de.retest.recheck.util.ObjectUtil.compare;
import static de.retest.recheck.util.ObjectUtil.isNullOrEmptyString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.util.ChecksumCalculator;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class AttributeDifference implements LeafDifference, Comparable<AttributeDifference>, Serializable {

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

	@XmlElement
	private final List<ElementIdentificationWarning> elementIdentificationWarnings;

	protected AttributeDifference() {
		// for JAXB
		key = null;
		expected = null;
		actual = null;
		attributeDifferenceId = null;
		elementIdentificationWarnings = new ArrayList<>();
	}

	public AttributeDifference( final String key, final Serializable expected, final Serializable actual ) {
		this.key = key;
		this.expected = expected;
		this.actual = actual;
		attributeDifferenceId = identifier();
		elementIdentificationWarnings = new ArrayList<>();
	}

	public String getKey() {
		return key;
	}

	public List<ElementIdentificationWarning> getElementIdentificationWarnings() {
		return elementIdentificationWarnings;
	}

	public void addElementIdentificationWarning( final ElementIdentificationWarning elementIdentificationWarning ) {
		elementIdentificationWarnings.add( elementIdentificationWarning );
	}

	public void addElementIdentificationWarnings( final Collection<? extends ElementIdentificationWarning> warnings ) {
		elementIdentificationWarnings.addAll( warnings );
	}

	public boolean hasElementIdentificationWarning() {
		return !elementIdentificationWarnings.isEmpty();
	}

	@Override
	public Serializable getExpected() {
		return (Serializable) expected;
	}

	@Override
	public Serializable getActual() {
		return (Serializable) actual;
	}

	public String getExpectedToString() {
		return Objects.toString( expected );
	}

	public String getActualToString() {
		return Objects.toString( actual );
	}

	public String identifier() {
		final String contents = Stream.of( key, actual, expected ) //
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
		} else if ( Objects.equals( fromAttribute, expected ) ) {
			return;
		}
		logger.warn(
				"Mismatch for attribute '{}': value from Golden Master '{}', value from test report '{}'. This could be due to a change in between.",
				key, fromAttribute, expected );
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

	@Override
	public int size() {
		return 0;
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		return Collections.emptyList();
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return Collections.emptyList();
	}
}
