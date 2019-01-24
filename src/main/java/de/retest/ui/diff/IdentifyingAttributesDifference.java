package de.retest.ui.diff;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.ui.descriptors.Attribute;
import de.retest.ui.descriptors.IdentifyingAttributes;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class IdentifyingAttributesDifference implements LeafDifference {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String differenceId;

	@XmlElement( name = "attribute" )
	private final List<Attribute> attributes;

	@XmlElement( name = "attributeDifference" )
	private final List<AttributeDifference> attributeDifferences;

	@SuppressWarnings( "unused" )
	private IdentifyingAttributesDifference() {
		// for JAXB
		differenceId = null;
		attributes = null;
		attributeDifferences = null;
	}

	IdentifyingAttributesDifference( final IdentifyingAttributes expectedIdentAttributes,
			final List<AttributeDifference> attributeDifferences ) {
		attributes = expectedIdentAttributes.getAttributes();
		this.attributeDifferences = attributeDifferences;
		differenceId = AttributeDifference.getSumIdentifier( attributeDifferences );
	}

	@Override
	public String toString() {
		final StringBuilder expectedDiff = new StringBuilder();
		final StringBuilder actualDiff = new StringBuilder();
		for ( final AttributeDifference attributeDifference : attributeDifferences ) {
			expectedDiff.append( " expected " ).append( attributeDifference.getKey() ).append( ": " )
					.append( attributeDifference.getExpected() );
			actualDiff.append( " actual " ).append( attributeDifference.getKey() ).append( ": " )
					.append( attributeDifference.getActual() );
		}
		return expectedDiff.toString().trim() + " - " + actualDiff.toString().trim();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		return Collections.emptyList();
	}

	@Override
	public Serializable getActual() {
		String actualDiff = "";
		for ( final AttributeDifference attributeDifference : attributeDifferences ) {
			actualDiff += " " + attributeDifference.getKey() + "=" + attributeDifference.getActual();
		}
		return actualDiff.trim();
	}

	@Override
	public Serializable getExpected() {
		String expectedDiff = "";
		for ( final AttributeDifference attributeDifference : attributeDifferences ) {
			expectedDiff += " " + attributeDifference.getKey() + "=" + attributeDifference.getExpected();
		}
		return expectedDiff.trim();
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return Collections.emptyList();
	}

	public List<AttributeDifference> getAttributes() {
		return attributeDifferences;
	}
}
