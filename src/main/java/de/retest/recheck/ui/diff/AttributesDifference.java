package de.retest.recheck.ui.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class AttributesDifference implements Difference {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String differenceId;

	@XmlElement
	private final List<AttributeDifference> differences;

	@SuppressWarnings( "unused" )
	private AttributesDifference() {
		// for JAXB
		differenceId = null;
		differences = new ArrayList<>();
	}

	public AttributesDifference( final List<AttributeDifference> differences ) {
		this.differences = differences;
		differenceId = AttributeDifference.getSumIdentifier( differences );
	}

	@Override
	public String toString() {
		return "{" + Joiner.on( ", " ).join( differences ) + "}";
	}

	@Override
	public int size() {
		return differences.size();
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		return Collections.emptyList();
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return Collections.emptyList();
	}

	public List<AttributeDifference> getDifferences() {
		return differences;
	}

	public String getIdentifier() {
		return differenceId;
	}
}
