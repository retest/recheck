package de.retest.ui.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.ui.image.Screenshot;
import de.retest.util.ChecksumCalculator;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class StateDifference implements Difference {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String differenceId;

	@XmlAnyElement( lax = true )
	private final List<RootElementDifference> differences;

	@XmlElement
	private final DurationDifference durationDifference;

	@SuppressWarnings( "unused" )
	private StateDifference() {
		// for JAXB
		differenceId = null;
		differences = new ArrayList<>();
		durationDifference = null;
	}

	public StateDifference( final List<RootElementDifference> differences,
			final DurationDifference durationDifference ) {
		differenceId = getSumIdentifier( differences );
		this.differences = Collections.unmodifiableList( differences );
		this.durationDifference = durationDifference;
	}

	public List<RootElementDifference> getStateDifference() {
		return differences;
	}

	public DurationDifference getDurationDifference() {
		return durationDifference;
	}

	@Override
	public String toString() {
		return differences.toString();
	}

	@Override
	public int size() {
		int size = 0;
		for ( final Difference difference : differences ) {
			size += difference.size();
		}
		return size;
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		return ElementDifferenceFinder.getNonEmptyDifferences( differences );
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return ElementDifferenceFinder.getElementDifferences( differences );
	}

	public String getIdentifier() {
		return differenceId;
	}

	public List<RootElementDifference> getRootElementDifferences() {
		return differences;
	}

	public List<Screenshot> getExpectedScreenshots() {
		final List<Screenshot> result = new ArrayList<>();
		for ( final RootElementDifference rootElementDifference : differences ) {
			result.add( rootElementDifference.getExpectedScreenshot() );
		}
		return result;
	}

	public List<Screenshot> getActualScreenshots() {
		final List<Screenshot> result = new ArrayList<>();
		for ( final RootElementDifference rootElementDifference : differences ) {
			result.add( rootElementDifference.getActualScreenshot() );
		}
		return result;
	}

	public static String getSumIdentifier( final List<RootElementDifference> differences ) {
		String result = "";
		for ( final RootElementDifference rootElementDifference : differences ) {
			result += " # " + rootElementDifference.getIdentifier();
		}
		return ChecksumCalculator.getInstance().sha256( result );
	}
}
