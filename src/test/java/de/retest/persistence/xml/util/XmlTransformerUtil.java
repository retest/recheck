package de.retest.persistence.xml.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.retest.persistence.xml.XmlTransformer;
import de.retest.persistence.xml.XmlTransformer.XmlTransformerConfig;

public class XmlTransformerUtil {

	public static String toXmlFragmentViaJAXB( final Object element, final Class<?>... additionalClazzes ) {
		final XmlTransformer xmlTransformer =
				new XmlTransformer( XmlTransformerConfig.CREATE_ONLY_FRAGMENT, additionalClazzes );
		return xmlTransformer.toXML( element );
	}

	public static String toLightweightXmlFragmentViaJAXB( final Object element, final Class<?>... additionalClazzes ) {
		final XmlTransformer xmlTransformer = new XmlTransformer( additionalClazzes,
				XmlTransformerConfig.CREATE_ONLY_FRAGMENT, XmlTransformerConfig.USE_LIGHTWEIGHT_XML );
		return xmlTransformer.toXML( element );
	}

	public static <T> String toXML( final T object, final Class<?>... additionalClazzes ) throws Exception {
		final XmlTransformer xmlTransformer =
				new XmlTransformer( additionalClazzes, XmlTransformerConfig.CREATE_ONLY_FRAGMENT );
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		xmlTransformer.toXML( object, out );
		return new String( out.toByteArray() );
	}

	public static <T> T roundTripXML( final T object, final Class<?>... classes ) throws Exception {
		final String xml = toXML( object, classes );
		return fromXML( xml, classes );
	}

	public static <T> T fromXML( final String xml, final Class<?>... additionalClazzes ) throws Exception {
		final XmlTransformer xmlTransformer =
				new XmlTransformer( additionalClazzes, XmlTransformerConfig.CREATE_ONLY_FRAGMENT );
		final ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
		return xmlTransformer.fromXML( in );
	}
}
