package de.retest.recheck.persistence.migration.transformers;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import de.retest.recheck.persistence.migration.XmlTransformer;

public class PathAndType2LowerCaseTransformer extends XmlTransformer {

	private boolean isInPath;
	private String path = "";
	private boolean isInType;
	private String type = "";

	@Override
	protected void reset() {
		isInPath = false;
		path = "";
		isInType = false;
		type = "";
	}

	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		final String tagname = "attribute";
		// <attribute xsi:type="pathAttribute" key="path">Window/JRootPane_0/JPane_1</attribute>
		if ( isStartElementNamed( event, tagname ) && hasAttribute( event, "key", "path" ) ) {
			isInPath = true;
		}

		if ( isInPath && event.isCharacters() ) {
			path += event.asCharacters().getData().trim();
			return;
		}

		if ( isInPath && isEndElementNamed( event, tagname ) ) {
			eventWriter.add( characters( path.toLowerCase() ) );
			isInPath = false;
			path = "";
		}

		if ( isStartElementNamed( event, tagname ) && hasAttribute( event, "key", "type" ) ) {
			isInType = true;
		}

		if ( isInType && event.isCharacters() ) {
			type += event.asCharacters().getData().trim();
			return;
		}

		if ( isInType && isEndElementNamed( event, tagname ) ) {
			eventWriter.add( characters( type.toLowerCase() ) );
			isInType = false;
			type = "";
		}

		eventWriter.add( event );
	}
}
