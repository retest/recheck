package de.retest.ui.diff;

import static de.retest.ui.diff.ElementDifference.getCopyWithFlattenedChildDifferenceHierarchy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ElementDifferenceAdapter extends XmlAdapter<ElementDifference, ElementDifference> {

	private final boolean renderLightweightXml;

	public ElementDifferenceAdapter() {
		renderLightweightXml = false;
	}

	public ElementDifferenceAdapter( final boolean renderLightweightXml ) {
		this.renderLightweightXml = renderLightweightXml;
	}

	@Override
	public ElementDifference marshal( final ElementDifference difference ) throws Exception {
		return renderLightweightXml ? getCopyWithFlattenedChildDifferenceHierarchy( difference ) : difference;
	}

	@Override
	public ElementDifference unmarshal( final ElementDifference difference ) throws Exception {
		return difference;
	}

}
