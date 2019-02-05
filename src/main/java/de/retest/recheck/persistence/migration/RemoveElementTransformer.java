package de.retest.recheck.persistence.migration;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class RemoveElementTransformer extends XmlTransformer {

	private final String parentElementName;
	private final String elementName;

	private boolean isInParent;
	private boolean isInElement;

	public RemoveElementTransformer( final String elementName ) {
		this( null, elementName );
	}

	public RemoveElementTransformer( final String parentElementName, final String elementName ) {
		this.parentElementName = parentElementName;
		this.elementName = elementName;
	}

	@Override
	protected void reset() {
		isInElement = false;
		isInParent = parentElementName == null;
	}

	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		if ( !isInParent && isStartElementNamed( event, parentElementName ) ) {
			isInParent = true;
			eventWriter.add( event );
			return;
		}
		if ( isInParent && isEndElementNamed( event, parentElementName ) ) {
			isInParent = false;
			eventWriter.add( event );
			return;
		}
		if ( isInParent && isStartElementNamed( event, elementName ) ) {
			isInElement = true;
			return;
		}
		if ( isInElement && isEndElementNamed( event, elementName ) ) {
			isInElement = false;
			return;
		}
		if ( isInElement ) {
			return;
		}
		eventWriter.add( event );
	}
}
