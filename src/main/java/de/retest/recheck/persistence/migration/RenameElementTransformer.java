package de.retest.recheck.persistence.migration;

import java.util.Iterator;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class RenameElementTransformer extends XmlTransformer {

	private final String oldElementName;
	private final String newElementName;

	public RenameElementTransformer( final String oldElementName, final String newElementName ) {
		this.oldElementName = oldElementName;
		this.newElementName = newElementName;
	}

	@Override
	protected void reset() {}

	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		if ( isStartElementNamed( event, oldElementName ) ) {
			final StartElement startAttributeEvent = (StartElement) event;
			@SuppressWarnings( "unchecked" )
			final Iterator<? extends Attribute> attributes = startAttributeEvent.getAttributes();
			eventWriter.add( startElementNamed( newElementName, attributes ) );
			return;
		}

		if ( isEndElementNamed( event, oldElementName ) ) {
			eventWriter.add( endElementNamed( newElementName ) );
			return;
		}

		eventWriter.add( event );
	}

}
