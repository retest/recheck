package de.retest.ui.descriptors;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RenderContainedElementsAdapter extends XmlAdapter<Element, Element> {

	private final boolean renderLightweightXml;

	public RenderContainedElementsAdapter() {
		renderLightweightXml = false;
	}

	public RenderContainedElementsAdapter( final boolean renderLightweightXml ) {
		this.renderLightweightXml = renderLightweightXml;
	}

	@Override
	public Element marshal( final Element descriptor ) throws Exception {
		if ( descriptor instanceof RootElement ) {
			return descriptor;
		}
		return renderLightweightXml ? null : descriptor;
	}

	private final Map<Element, Element> elements = new HashMap<>();

	@Override
	public Element unmarshal( final Element descriptor ) throws Exception {
		if ( descriptor.identifyingAttributes == null ) {
			return descriptor;
		}

		if ( elements.containsKey( descriptor ) ) {
			return elements.get( descriptor );
		}

		elements.put( descriptor, descriptor );
		return descriptor;
	}

}
