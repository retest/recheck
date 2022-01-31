package de.retest.recheck.ui.descriptors;

import java.util.ArrayList;
import java.util.List;

import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.review.ActionChangeSet;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

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
		super( retestId, null, identifyingAttributes, attributes, screenshot );
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

		final Screenshot newScreenshot = actionChangeSet.getScreenshot().getScreenshot( identifyingAttributes );

		final List<Element> newContainedElements = createNewElementList( actionChangeSet, newIdentAttributes );

		final RootElement rootElement =
				new RootElement( retestId, newIdentAttributes, newAttributes, newScreenshot, screen, screenId, title );
		rootElement.addChildren( newContainedElements );
		return rootElement;
	}

	@Override
	public String toString() {
		if ( title != null ) {
			return title;
		}
		return super.toString();
	}
}
