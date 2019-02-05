package de.retest.recheck.persistence.migration.transformers;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import de.retest.recheck.persistence.migration.XmlTransformer;

public class Path2XPathTransformer extends XmlTransformer {

	private static final Pattern countBeforeSeparator = Pattern.compile( "_([0-9]+)(\\/|\\z){1}" );

	private boolean isInPath;
	private String path = "";
	private boolean isInSuffix;
	private String suffix = "";

	@Override
	protected void reset() {
		isInPath = false;
		path = "";
		isInSuffix = false;
		suffix = "";
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
			eventWriter.add( characters( toXPath( path ) ) );
			isInPath = false;
			path = "";
		}

		// <attribute xsi:type="suffixAttribute" key="suffix">1</attribute>
		if ( isStartElementNamed( event, "attribute" ) && hasAttribute( event, "key", "suffix" ) ) {
			isInSuffix = true;
		}

		if ( isInSuffix && event.isCharacters() ) {
			suffix += event.asCharacters().getData().trim();
			return;
		}

		if ( isEndElementNamed( event, "attribute" ) ) {
			try {
				eventWriter.add( characters( valueOf( parseInt( suffix ) + 1 ) ) );
			} catch ( final NumberFormatException exc ) {
				eventWriter.add( characters( suffix ) );
			}
			isInSuffix = false;
			suffix = "";
		}

		eventWriter.add( event );
	}

	protected static String toXPath( final String path ) {
		String result = path;
		// replace and increase by 1
		final Matcher matcher = countBeforeSeparator.matcher( result );
		final StringBuffer sb = new StringBuffer();
		while ( matcher.find() ) {
			matcher.appendReplacement( sb,
					"[" + valueOf( parseInt( matcher.group( 1 ) ) + 1 ) + "]" + matcher.group( 2 ) );
		}
		matcher.appendTail( sb );
		result = sb.toString();

		// treat popup speciality
		result = result.replaceAll( "popup_([^\\/]+)(\\/|\\z){1}", "popup_$1[1]$2" );

		return result;
	}

}
