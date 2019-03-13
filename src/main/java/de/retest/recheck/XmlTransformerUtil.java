package de.retest.recheck;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.persistence.xml.XmlTransformer.XmlTransformerConfig;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.ui.descriptors.SutState;

public class XmlTransformerUtil {

	public static XmlTransformer getXmlTransformer() {
		final Set<Class<?>> xmlDataClasses = new HashSet<>();
		xmlDataClasses.add( TestReport.class );
		xmlDataClasses.add( SutState.class );
		return new XmlTransformer( xmlDataClasses );
	}

	public static <T> String toXML( final T object, final Class<?>... additionalClazzes ) throws Exception {
		final XmlTransformer xmlTransformer =
				new XmlTransformer( additionalClazzes, XmlTransformerConfig.CREATE_ONLY_FRAGMENT );
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		xmlTransformer.toXML( object, out );
		return new String( out.toByteArray() );
	}

	public static String toXmlFragmentViaJAXB( final Object element, final Class<?>... additionalClazzes ) {
		final XmlTransformer xmlTransformer =
				new XmlTransformer( XmlTransformerConfig.CREATE_ONLY_FRAGMENT, additionalClazzes );
		return xmlTransformer.toXML( element );
	}
}
