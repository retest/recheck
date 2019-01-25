package de.retest.ui.descriptors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IdentifyingAttributesAdapter extends XmlAdapter<IdentifyingAttributes, IdentifyingAttributes> {

	private final boolean renderLightweightXml;

	public IdentifyingAttributesAdapter() {
		renderLightweightXml = false;
	}

	public IdentifyingAttributesAdapter( final boolean renderLightweightXml ) {
		this.renderLightweightXml = renderLightweightXml;
	}

	@Override
	public IdentifyingAttributes marshal( final IdentifyingAttributes identifyingAttributes ) throws Exception {
		return renderLightweightXml ? null : identifyingAttributes;
	}

	@Override
	public IdentifyingAttributes unmarshal( final IdentifyingAttributes identifyingAttributes ) throws Exception {
		return identifyingAttributes;
	}

}
