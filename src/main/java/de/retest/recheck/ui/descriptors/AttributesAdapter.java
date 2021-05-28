package de.retest.recheck.ui.descriptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class AttributesAdapter
		extends XmlAdapter<AttributesAdapter.PersistableAttributes, SortedMap<String, Attribute>> {

	@XmlRootElement
	public static class PersistableAttributes implements Serializable {
		private static final long serialVersionUID = 1L;

		@XmlElement
		public List<Attribute> attribute = new ArrayList<>();
	}

	@Override
	public PersistableAttributes marshal( final SortedMap<String, Attribute> toPersist ) throws Exception {
		if ( null == toPersist ) {
			return null;
		}
		final PersistableAttributes persistableAttributes = new PersistableAttributes();
		persistableAttributes.attribute.addAll( toPersist.values() );
		return persistableAttributes;
	}

	@Override
	public SortedMap<String, Attribute> unmarshal( final PersistableAttributes toLoad ) throws Exception {
		if ( null == toLoad ) {
			return null;
		}
		final SortedMap<String, Attribute> result = new TreeMap<>();
		for ( final Attribute attribute : toLoad.attribute ) {
			result.put( attribute.getKey(), attribute );
		}
		return result;
	}
}
