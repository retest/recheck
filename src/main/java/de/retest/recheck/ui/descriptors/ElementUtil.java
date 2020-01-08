package de.retest.recheck.ui.descriptors;

import java.util.ArrayList;
import java.util.List;

public class ElementUtil {

	private ElementUtil() {}

	public static List<Element> flattenAllElements( final List<Element> elements ) {
		final List<Element> flattened = new ArrayList<>();

		for ( final Element element : elements ) {
			flattened.add( element );
			flattened.addAll( flattenChildElements( element ) );
		}

		return flattened;
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
