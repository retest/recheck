package de.retest.elementcollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.retest.ui.descriptors.Element;

public class ExcludedAttributesAdapter extends XmlAdapter<ExcludedAttributes, Map<Element, Set<String>>> {

	@Override
	public ExcludedAttributes marshal( final Map<Element, Set<String>> toPersist ) throws Exception {
		final List<ComponentAttributes> result = new ArrayList<>();
		for ( final Map.Entry<Element, Set<String>> excludedAttribute : toPersist.entrySet() ) {
			result.add( new ComponentAttributes( excludedAttribute.getKey(), excludedAttribute.getValue() ) );
		}
		return new ExcludedAttributes( result );
	}

	@Override
	public Map<Element, Set<String>> unmarshal( final ExcludedAttributes toLoad ) throws Exception {
		final Map<Element, Set<String>> result = new HashMap<>();
		if ( toLoad.getAttributes() != null ) {
			for ( final ComponentAttributes compAttributes : toLoad.getAttributes() ) {
				result.put( compAttributes.getElement(), compAttributes.convertAttributes() );
			}
		}
		return result;
	}

}
