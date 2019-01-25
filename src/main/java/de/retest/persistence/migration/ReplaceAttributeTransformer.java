package de.retest.persistence.migration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ReplaceAttributeTransformer extends XmlTransformer {

	private final String tagname;
	private final String attributeName;
	private final String value;
	private final String newValue;

	public ReplaceAttributeTransformer( final String tagname, final String attributeName, final String value,
			final String newValue ) {
		this.tagname = tagname;
		this.attributeName = attributeName;
		this.value = value;
		this.newValue = newValue;
	}

	@Override
	public void reset() {}

	@SuppressWarnings( "unchecked" )
	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		if ( isStartElementNamed( event, tagname ) ) {

			final StartElement startElement = event.asStartElement();
			eventWriter.add( newline() );
			eventWriter.add( eventFactory.createStartElement( startElement.getName(),
					convertAttributes( startElement.getAttributes() ), startElement.getNamespaces() ) );
			return;
		}

		eventWriter.add( event );
	}

	private Iterator<Attribute> convertAttributes( final Iterator<Attribute> attributes ) {
		final List<Attribute> newAttributes = new ArrayList<>();
		while ( attributes.hasNext() ) {
			final Attribute attribute = attributes.next();
			final String name = attribute.getName().getLocalPart();
			if ( name.equals( attributeName ) && attribute.getValue().matches( value ) ) {
				newAttributes.add( eventFactory.createAttribute( attribute.getName(), newValue ) );
			} else {
				newAttributes.add( attribute );
			}
		}
		return newAttributes.iterator();
	}

}
