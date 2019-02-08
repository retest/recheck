package de.retest.recheck.persistence.migration.transformers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import de.retest.recheck.persistence.migration.XmlTransformer;

public class AddRetestIdTestTransformer extends XmlTransformer {

	public static class RetestIdCreator {
		public String retestId() {
			return UUID.randomUUID().toString();
		}
	}

	private final RetestIdCreator idCreator;

	public AddRetestIdTestTransformer() {
		this( new RetestIdCreator() );
	}

	public AddRetestIdTestTransformer( final RetestIdCreator idCreator ) {
		this.idCreator = idCreator;
	}

	@Override
	protected void reset() {}

	@SuppressWarnings( "unchecked" )
	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		// - <targetcomponent>
		// + <targetcomponent retestId="id">
		if ( isStartElementNamed( event, "targetcomponent" ) ) {
			eventWriter.add( startElementNamed( "targetcomponent", attribute( "retestId", idCreator.retestId() ) ) );
			return;
		}

		// - <element>
		// + <element retestId="id">
		if ( isStartElementNamed( event, "element" ) ) {
			eventWriter.add( startElementNamed( "element", attribute( "retestId", idCreator.retestId() ) ) );
			return;
		}

		// - <bestMatch>
		// + <bestMatch retestId="id">
		if ( isStartElementNamed( event, "bestMatch" ) ) {
			eventWriter.add( startElementNamed( "bestMatch", attribute( "retestId", idCreator.retestId() ) ) );
			return;
		}

		// - <containedComponents>
		// + <containedComponents retestId="bird">
		if ( isStartElementNamed( event, "containedComponents" ) ) {
			eventWriter
					.add( startElementNamed( "containedComponents", attribute( "retestId", idCreator.retestId() ) ) );
			return;
		}

		if ( isStartElementNamed( event, "descriptors" ) ) {
			final List<Attribute> attributes = new ArrayList<>();
			attributes.add( attribute( "retestId", idCreator.retestId() ) );
			attributes.addAll( toList( event.asStartElement().getAttributes() ) );
			eventWriter.add( startElementNamed( "descriptors", attributes.iterator() ) );
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
