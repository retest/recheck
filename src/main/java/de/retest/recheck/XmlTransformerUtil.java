package de.retest.recheck;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.persistence.xml.XmlTransformerConfiguration;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.ui.descriptors.SutState;

public class XmlTransformerUtil {

	private XmlTransformerUtil() {}

	public static XmlTransformer getXmlTransformer() {
		final Set<Class<?>> xmlDataClasses = new HashSet<>();
		xmlDataClasses.add( TestReport.class );
		xmlDataClasses.add( SutState.class );
		return new XmlTransformer( xmlDataClasses );
	}

	public static <T> String toXML( final T object, final Class<?>... additionalClazzes ) throws Exception {
		final XmlTransformerConfiguration config = XmlTransformerConfiguration.builder() //
				.onlyFragment( true ) //
				.build();
		final XmlTransformer xmlTransformer = new XmlTransformer( config, additionalClazzes );
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		xmlTransformer.toXML( object, out );
		return new String( out.toByteArray() );
	}

	public static String toXmlFragmentViaJAXB( final Object element, final Class<?>... additionalClazzes ) {
		final XmlTransformerConfiguration config = XmlTransformerConfiguration.builder() //
				.onlyFragment( true ) //
				.build();
		final XmlTransformer xmlTransformer = new XmlTransformer( config, additionalClazzes );
		return xmlTransformer.toXML( element );
	}
}
