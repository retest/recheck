package de.retest.recheck.persistence.xml.util;

import javax.xml.bind.Marshaller;

import de.retest.recheck.ui.descriptors.IdentifyingAttributesAdapter;
import de.retest.recheck.ui.descriptors.RenderContainedElementsAdapter;
import de.retest.recheck.ui.descriptors.StateAttributesAdapter;

public class XmlUtil {

	private XmlUtil() {}

	public static String clean( final Object input ) {
		if ( input == null ) {
			return null;
		}
		String result = input.toString().trim();
		result = result.replace( "&", "&amp;" );
		result = result.replace( "<", "&lt;" );
		result = result.replace( ">", "&gt;" );
		result = result.replace( "\"", "'" );
		return result;
	}

	public static void addLightWeightAdapter( final Marshaller marshaller ) {
		marshaller.setAdapter( new RenderContainedElementsAdapter( true ) );
		marshaller.setAdapter( new StateAttributesAdapter( true ) );
		marshaller.setAdapter( new IdentifyingAttributesAdapter( true ) );
	}
}
