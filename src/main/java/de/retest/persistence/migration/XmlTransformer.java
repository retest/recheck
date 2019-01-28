package de.retest.persistence.migration;

import static de.retest.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.retest.util.DeleteOnCloseFileInputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public abstract class XmlTransformer {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( XmlTransformer.class );

	private static final String BR = System.getProperty( "line.separator" );

	protected final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

	public final InputStream transform( final InputStream inputStream ) {
		try {
			// Since these files can become pretty big, storing them in memory might lead to OutOfMemoryErrors.
			final File tmpFile = File.createTempFile( "retest-migration-", ".xml.lz4" );
			tmpFile.deleteOnExit();
			logger.debug( "Creating temporary file {} for XML migration. File will be deleted upon exit.",
					canonicalPathQuietly( tmpFile ) );

			convertAndWriteToFile( inputStream, tmpFile );
			return new LZ4BlockInputStream( new DeleteOnCloseFileInputStream( tmpFile ) );
		} catch ( final IOException e ) {
			throw new RuntimeException( e );
		}
	}

	private void convertAndWriteToFile( final InputStream inputStream, final File tmpFile ) throws IOException {
		try ( final LZ4BlockOutputStream out = new LZ4BlockOutputStream( new FileOutputStream( tmpFile ) ) ) {
			reset();

			final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			final XMLEventReader eventReader =
					inputFactory.createXMLEventReader( inputStream, StandardCharsets.UTF_8.name() );
			final XMLEventWriter eventWriter =
					XMLOutputFactory.newInstance().createXMLEventWriter( out, StandardCharsets.UTF_8.name() );

			while ( eventReader.hasNext() ) {
				final XMLEvent nextEvent = eventReader.nextEvent();
				convert( nextEvent, eventWriter );
			}
			eventReader.close();
			eventWriter.flush();
			eventWriter.close();

		} catch ( final XMLStreamException | FactoryConfigurationError e ) {
			throw new RuntimeException( e );
		}
	}

	protected abstract void reset();

	public abstract void convert( final XMLEvent event, XMLEventWriter eventWriter ) throws XMLStreamException;

	protected Characters newline() {
		return eventFactory.createCharacters( BR );
	}

	protected boolean isStartElementNamed( final XMLEvent event, final String tagname ) {
		return event.isStartElement() && event.asStartElement().getName().getLocalPart().equals( tagname );
	}

	protected boolean hasAttribute( final XMLEvent event, final String key, final String value ) {
		final Attribute attribute = event.asStartElement().getAttributeByName( new QName( key ) );
		return attribute != null && attribute.getValue().equals( value );
	}

	protected boolean isEndElementNamed( final XMLEvent event, final String tagname ) {
		return event.isEndElement() && event.asEndElement().getName().getLocalPart().equals( tagname );
	}

	protected StartElement startElementNamed( final String tagname ) {
		return eventFactory.createStartElement( new QName( tagname ), null, null );
	}

	protected StartElement startElementNamed( final String tagname, final Iterator<? extends Attribute> attributes ) {
		return eventFactory.createStartElement( new QName( tagname ), attributes, null );
	}

	protected XMLEvent startElementNamed( final String tagname, final Attribute... attributes ) {
		return startElementNamed( tagname, Arrays.asList( attributes ).iterator() );
	}

	protected Attribute attribute( final String attributeName, final String value ) {
		return eventFactory.createAttribute( new QName( attributeName ), value );
	}

	protected EndElement endElementNamed( final String tagname ) {
		return eventFactory.createEndElement( new QName( tagname ), null );
	}

	protected Characters characters( final String contents ) {
		return eventFactory.createCharacters( contents );
	}
}
