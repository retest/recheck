package de.retest.recheck.persistence.migration.transformers;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import de.retest.recheck.persistence.migration.XmlTransformer;

public class WindowSuffixTransformer extends XmlTransformer {

	private boolean isInPath;

	private boolean isWindowPath;
	private boolean isInWindowSuffix;

	@Override
	protected void reset() {
		isInPath = false;
		isWindowPath = false;
		isInWindowSuffix = false;
	}

	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		if ( updatePath( event ) ) {
			eventWriter.add( event );
			return;
		}
		if ( updatePath( event, eventWriter ) ) {
			return;
		}
		if ( updateWindowsSuffix( event, eventWriter ) ) {
			return;
		}
		eventWriter.add( event );
	}

	protected boolean updatePath( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		if ( isInPath && event.isCharacters() ) {
			final Characters characters = event.asCharacters();
			final String window = characters.getData();

			isWindowPath = "Window".equals( window );

			final String replace = window.replaceFirst( "Window", "Window_0" );
			eventWriter.add( characters( replace ) );
			return true;
		}
		return false;
	}

	private boolean updatePath( final XMLEvent event ) {
		if ( isStartElementNamed( event, "attribute" ) && hasAttribute( event, "key", "path" ) ) {
			isInPath = true;
			return true;
		}
		if ( isInPath && isEndElementNamed( event, "attribute" ) ) {
			isInPath = false;
			return true;
		}
		return false;
	}

	private boolean updateWindowsSuffix( final XMLEvent event, final XMLEventWriter eventWriter )
			throws XMLStreamException {
		if ( isWindowPath && isStartElementNamed( event, "attribute" ) && hasAttribute( event, "key", "suffix" ) ) {
			isInWindowSuffix = true;

			eventWriter.add( event );
			eventWriter.add( characters( "0" ) );

			return true;
		}
		if ( isInWindowSuffix && isEndElementNamed( event, "attribute" ) ) {
			isInWindowSuffix = false;
			eventWriter.add( event );
			return true;
		}
		return false;
	}
}
