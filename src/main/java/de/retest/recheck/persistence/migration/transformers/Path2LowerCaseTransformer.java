package de.retest.recheck.persistence.migration.transformers;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import de.retest.recheck.persistence.migration.XmlTransformer;

public class Path2LowerCaseTransformer extends XmlTransformer {

	private boolean isInPath;
	private String path = "";

	@Override
	protected void reset() {
		isInPath = false;
		path = "";
	}

	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		// <attribute xsi:type="pathAttribute" key="path">Window/JRootPane_0/JPane_1</attribute>
		if ( isStartElementNamed( event, "attribute" ) && hasAttribute( event, "key", "path" ) ) {
			isInPath = true;
		}

		if ( isInPath && event.isCharacters() ) {
			path += event.asCharacters().getData().trim();
			return;
		}

		if ( isInPath && isEndElementNamed( event, "attribute" ) ) {
			eventWriter.add( characters( path.toLowerCase() ) );
			isInPath = false;
			path = "";
		}

		eventWriter.add( event );
	}
}
