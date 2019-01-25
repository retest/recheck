package de.retest.persistence.xml.util;

import javax.xml.bind.Marshaller;

import de.retest.ui.descriptors.IdentifyingAttributesAdapter;
import de.retest.ui.descriptors.RenderContainedElementsAdapter;
import de.retest.ui.descriptors.StateAttributesAdapter;

public class XmlUtil {

	public static String clean( final Object input ) {
		if ( input == null ) {
			return null;
		}
		String result = input.toString().trim();
		result = result.replaceAll( "&", "&amp;" );
		result = result.replaceAll( "<", "&lt;" );
		result = result.replaceAll( ">", "&gt;" );
		result = result.replaceAll( "\"", "'" );
		return result;
	}

	public static void addLightWeightAdapter( final Marshaller marshaller ) {
		marshaller.setAdapter( new RenderContainedElementsAdapter( true ) );
		marshaller.setAdapter( new StateAttributesAdapter( true ) );
		marshaller.setAdapter( new IdentifyingAttributesAdapter( true ) );
	}
}
