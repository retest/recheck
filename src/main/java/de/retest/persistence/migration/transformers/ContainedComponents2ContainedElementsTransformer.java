package de.retest.persistence.migration.transformers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import de.retest.persistence.migration.XmlTransformer;

public class ContainedComponents2ContainedElementsTransformer extends XmlTransformer {

	@Override
	protected void reset() {}

	@SuppressWarnings( "unchecked" )
	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		if ( isStartElementNamed( event, "containedComponents" ) ) {
			final List<Attribute> attributes = new ArrayList<>();
			attributes.addAll( toList( event.asStartElement().getAttributes() ) );
			eventWriter.add( startElementNamed( "containedElements", attributes.iterator() ) );
			return;
		}

		if ( isEndElementNamed( event, "containedComponents" ) ) {
			eventWriter.add( endElementNamed( "containedElements" ) );
			return;
		}

		eventWriter.add( event );
	}

	private Collection<Attribute> toList( final Iterator<Attribute> attributes ) {
		final List<Attribute> list = new ArrayList<>();
		attributes.forEachRemaining( list::add );
		return list;
	}
}
