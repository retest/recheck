package de.retest.ui.descriptors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StateAttributesAdapter extends XmlAdapter<Attributes, Attributes> {

	private final boolean renderLightweightXml;

	public StateAttributesAdapter() {
		renderLightweightXml = false;
	}

	public StateAttributesAdapter( final boolean renderLightweightXml ) {
		this.renderLightweightXml = renderLightweightXml;
	}

	@Override
	public Attributes marshal( final Attributes attributes ) throws Exception {
		return renderLightweightXml ? null : attributes;
	}

	@Override
	public Attributes unmarshal( final Attributes attributes ) throws Exception {
		return attributes;
	}

}
