package de.retest.ui.diff;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.util.DurationUtil;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class DurationDifference implements LeafDifference {

	// TODO Allow percentage difference as well

	private static final long serialVersionUID = 1L;

	@XmlElement
	private final AttributeDifference difference;

	private DurationDifference() {
		// for JAXB
		difference = null;
	}

	private DurationDifference( final long expected, final long actual ) {
		difference = new AttributeDifference( "duration", expected, actual );
	}

	public static DurationDifference differenceFor( final long expected, final long actual ) {
		return DurationUtil.ignore( expected, actual ) ? null : new DurationDifference( expected, actual );
	}

	@Override
	public String toString() {
		return "[" + DurationUtil
				.durationToFormattedString( (Long) difference.getActual() - (Long) difference.getExpected() ) + "]";
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
		return difference.getActual();
	}

	@Override
	public Serializable getExpected() {
		return difference.getExpected();
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return Collections.emptyList();
	}
}
