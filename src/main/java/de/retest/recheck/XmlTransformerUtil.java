package de.retest.recheck;

import java.util.HashSet;
import java.util.Set;

import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.report.ReplayResult;
import de.retest.recheck.ui.descriptors.SutState;

public class XmlTransformerUtil {
	public static XmlTransformer getXmlTransformer() {
		final Set<Class<?>> xmlDataClasses = new HashSet<>();
		xmlDataClasses.add( ReplayResult.class );
		xmlDataClasses.add( SutState.class );
		return new XmlTransformer( xmlDataClasses );
	}
}
