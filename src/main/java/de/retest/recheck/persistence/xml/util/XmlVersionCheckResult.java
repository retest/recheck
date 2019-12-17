package de.retest.recheck.persistence.xml.util;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.persistence.xml.ReTestXmlDataContainer;
import de.retest.recheck.util.ReflectionUtilities;

public class XmlVersionCheckResult {

	private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

	public final int oldVersion;
	public final String oldDataType;
	public final Persistable newDataTypeInstance;

	private XmlVersionCheckResult( final String dataType, final int version ) {
		oldVersion = version;
		oldDataType = dataType;
		newDataTypeInstance = ReflectionUtilities.createNewInstanceOrNull( Persistable.class, dataType );
	}

	public static XmlVersionCheckResult create( final BufferedInputStream in ) throws IOException {
		// JAXB needs the whole XML so we must reset the stream to start
		in.mark( 18192 );

		final XMLStreamReader parser = createParserAndPointItToRootXmlElement( in );

		preventLoadingOfOutdatedFile( parser );
		ensureThatContainerIsRootElement( parser );

		final XmlVersionCheckResult checkResult = extractCheckResultValuesFromXml( parser );

		in.reset(); // see mark() above

		return checkResult;
	}

	private static XMLStreamReader createParserAndPointItToRootXmlElement( final BufferedInputStream in ) {
		try {
			final XMLStreamReader parser = FACTORY.createXMLStreamReader( in );
			parser.next(); // first is the xml tag
			return parser;
		} catch ( final XMLStreamException e ) {
			throw new IllegalArgumentException( "The input don't look like valid retest XML content!", e );
		}
	}

	private static void preventLoadingOfOutdatedFile( final XMLStreamReader parser ) {
		// Old retest version have a comment with version as first element
		if ( parser.getEventType() == XMLStreamConstants.COMMENT
				&& parser.getText().contains( "Serialized with ReTest" ) ) {
			throw new IllegalArgumentException(
					"This file looks like outdated retest XML file, please call support to update!" );
		}
	}

	private static void ensureThatContainerIsRootElement( final XMLStreamReader parser ) {
		while ( parser.getEventType() == XMLStreamConstants.COMMENT ) {
			try {
				parser.next();
			} catch ( final XMLStreamException exc ) {
				throw new RuntimeException( exc );
			}
		}
		if ( !ReTestXmlDataContainer.class.getSimpleName().equalsIgnoreCase( parser.getName().toString() ) ) {
			throw new IllegalArgumentException(
					"This file doesn't look like a retest XML file, unexpected first tag in XML: "
							+ parser.getName().toString() );
		}
	}

	private static XmlVersionCheckResult extractCheckResultValuesFromXml( final XMLStreamReader parser ) {
		final String dataType;
		final int version;
		try {

			dataType = parser.getAttributeValue( null, ReTestXmlDataContainer.DATA_TYPE_FIELD );
			final String versionString =
					parser.getAttributeValue( null, ReTestXmlDataContainer.DATA_TYPE_VERSION_FIELD );
			version = Integer.parseInt( versionString );

		} catch ( final IllegalStateException | NumberFormatException e ) {
			throw new IllegalArgumentException( "Invalid XML document!", e );
		}
		return new XmlVersionCheckResult( dataType, version );
	}

	public boolean isCompatible() {
		return newDataTypeInstance != null && newDataTypeInstance.version() == oldVersion;
	}
}
