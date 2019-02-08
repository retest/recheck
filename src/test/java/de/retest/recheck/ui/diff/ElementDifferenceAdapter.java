package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.diff.ElementDifference.getCopyWithFlattenedChildDifferenceHierarchy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.retest.recheck.ui.diff.ElementDifference;

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
