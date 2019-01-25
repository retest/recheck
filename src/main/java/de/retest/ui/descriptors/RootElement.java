package de.retest.ui.descriptors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.ui.image.Screenshot;
import de.retest.ui.review.ActionChangeSet;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class RootElement extends Element {

	private static final long serialVersionUID = 2L;

	@XmlAttribute
	private final int screenId;
	@XmlAttribute
	private final String screen;
	@XmlAttribute
	private final String title;

	@SuppressWarnings( "unused" )
	private RootElement() {
		// for JAXB
		screenId = 0;
		screen = null;
		title = null;
	}

	public RootElement( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final Attributes attributes, final Screenshot screenshot, final String screen, final int screenId,
			final String title ) {
		super( retestId, new Element(), identifyingAttributes, attributes, screenshot );
		this.screen = screen;
		this.screenId = screenId;
		this.title = title;
	}

	public String getScreen() {
		return screen;
	}

	public int getScreenId() {
		return screenId;
	}

	public String getTitle() {
		return title;
	}

	public static List<Screenshot> getScreenshots( final List<RootElement> windows ) {
		final List<Screenshot> result = new ArrayList<>();
		for ( final RootElement rootElement : windows ) {
			result.add( rootElement.getScreenshot() );
		}
		return result;
	}

	@Override
	public RootElement applyChanges( final ActionChangeSet actionChangeSet ) {
		if ( actionChangeSet == null ) {
			return this;
		}

		final IdentifyingAttributes newIdentAttributes;
		newIdentAttributes = identifyingAttributes
				.applyChanges( actionChangeSet.getIdentAttributeChanges().getAll( identifyingAttributes ) );

		final Attributes newAttributes =
				attributes.applyChanges( actionChangeSet.getAttributesChanges().getAll( identifyingAttributes ) );

		final List<Element> newContainedElements = createNewElementList( actionChangeSet, newIdentAttributes );

		final RootElement rootElement =
				new RootElement( retestId, newIdentAttributes, newAttributes, screenshot, screen, screenId, title );
		rootElement.addChildren( newContainedElements );
		return rootElement;
	}
}
