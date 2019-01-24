package de.retest.ui.descriptors;

import java.util.ArrayList;
import java.util.List;

import de.retest.ui.components.Component;
import de.retest.ui.components.ComponentContainer;
import de.retest.ui.components.RootContainer;
import de.retest.ui.image.Screenshot;

public class ElementUtil {

	public static List<Element> flattenAllElements( final List<Element> elements ) {
		final List<Element> flattened = new ArrayList<>();

		for ( final Element element : elements ) {
			flattened.add( element );
			flattened.addAll( flattenChildElements( element ) );
		}

		return flattened;
	}

	public static Element toElement( final Component<?> component ) {
		final IdentifyingAttributes identifyingAttributes = component.retrieveIdentifyingAttributes();
		final Attributes attributes = component.retrieveAttributes();
		final Screenshot screenshot = component.createScreenshot();
		final List<Element> containedComponents = new ArrayList<>();
		if ( component instanceof ComponentContainer<?> ) {
			for ( final Component<?> containedComponent : ((ComponentContainer<?>) component).getChildComponents() ) {
				containedComponents.add( containedComponent.getElement() );
			}
		}
		final String retestId = RetestIdProviderUtil.getRetestId( identifyingAttributes );
		final Element element =
				Element.create( retestId, new Element(), identifyingAttributes, attributes, screenshot );
		element.addChildren( containedComponents );
		return element;
	}

	public static RootElement toRootElement( final RootContainer<?> window ) {
		final IdentifyingAttributes identifyingAttributes = window.retrieveIdentifyingAttributes();
		final Attributes attributes = window.retrieveAttributes();
		final Screenshot screenshot = window.createScreenshot();
		final List<Element> containedComponents = new ArrayList<>();
		for ( final Component<?> containedComponent : ((ComponentContainer<?>) window).getChildComponents() ) {
			containedComponents.add( containedComponent.getElement() );
		}
		final String screen = window.getScreenId();
		final int screenId = screen.hashCode();
		final String title = window.getText();
		final String retestId = RetestIdProviderUtil.getRetestId( identifyingAttributes );
		final RootElement rootElement =
				new RootElement( retestId, identifyingAttributes, attributes, screenshot, screen, screenId, title );
		rootElement.addChildren( containedComponents );
		return rootElement;
	}

	public static List<Element> flattenChildElements( final Element element ) {
		final List<Element> flattened = new ArrayList<>();

		for ( final Element childElement : element.getContainedElements() ) {
			flattened.add( childElement );
			flattened.addAll( flattenChildElements( childElement ) );
		}

		return flattened;
	}

	public static boolean pathEquals( final Element element0, final Element element1 ) {
		return element0.getIdentifyingAttributes().getPathTyped()
				.equals( element1.getIdentifyingAttributes().getPathTyped() );
	}
}
