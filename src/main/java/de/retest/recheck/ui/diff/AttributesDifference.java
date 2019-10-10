package de.retest.recheck.ui.diff;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Joiner;

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
		differences = null;
	}

	public AttributesDifference( final List<AttributeDifference> differences ) {
		this.differences = Collections.unmodifiableList( differences );
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
