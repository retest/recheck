package de.retest.recheck.ui.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.util.ChecksumCalculator;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class StateDifference implements Difference {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String differenceId;

	@XmlAnyElement( lax = true )
	private final List<RootElementDifference> differences;

	@SuppressWarnings( "unused" )
	private StateDifference() {
		// for JAXB
		differenceId = null;
		differences = new ArrayList<>();
	}

	public StateDifference( final List<RootElementDifference> differences ) {
		differenceId = getSumIdentifier( differences );
		this.differences = Collections.unmodifiableList( differences );
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

	private static String getSumIdentifier( final List<RootElementDifference> differences ) {
		return differences.stream() //
				.map( RootElementDifference::getIdentifier ) //
				.collect( Collectors.collectingAndThen( Collectors.joining( " # " ),
						ChecksumCalculator.getInstance()::sha256 ) );
	}
}
