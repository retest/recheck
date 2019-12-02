package de.retest.recheck.ui.descriptors;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.image.Screenshot;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SutStateFilter {
	private final Filter filter;

	public SutState filter( final SutState sutState ) {
		if ( filterNothing() ) {
			return sutState;
		}
		final List<RootElement> rootElements = sutState.getRootElements();
		final List<RootElement> filteredRootElements = rootElements.stream() //
				.map( this::filter ) //
				.collect( Collectors.toList() );
		final SutState newSutState = new SutState( filteredRootElements, sutState.getMetadata() );
		return newSutState;
	}

	public RootElement filter( final RootElement rootElement ) {
		if ( filterNothing() ) {
			return rootElement;
		}
		final IdentifyingAttributes newIdentifyingAttributes =
				filter( rootElement, rootElement.getIdentifyingAttributes() );
		final Attributes newAttributes = filter( rootElement, rootElement.getAttributes() );
		final String retestId = rootElement.getRetestId();
		final Screenshot screenshot = rootElement.getScreenshot();
		final String screen = rootElement.getScreen();
		final int screenId = rootElement.getScreenId();
		final String title = rootElement.getTitle();
		final List<Element> containedElements = rootElement.getContainedElements();

		final RootElement newRootElement = new RootElement( retestId, newIdentifyingAttributes, newAttributes,
				screenshot, screen, screenId, title );

		final List<Element> newContainedElements = filterContainedElements( newRootElement, containedElements );
		newRootElement.addChildren( newContainedElements );
		return newRootElement;
	}

	IdentifyingAttributes filter( final Element element, final IdentifyingAttributes identifyingAttributes ) {
		final List<Attribute> attributes = identifyingAttributes.getAttributes();

		final List<Attribute> filteredAttributes = attributes.stream() //
				.filter( attribute -> !filter.matches( element, attribute.getKey() ) ) //
				.collect( Collectors.toList() );
		return new IdentifyingAttributes( filteredAttributes );
	}

	Attributes filter( final Element element, final Attributes attributes ) {
		final MutableAttributes filteredAttributes = new MutableAttributes();

		StreamSupport.stream( attributes.spliterator(), false ) //
				.filter( attribute -> !filter.matches( element, attribute.getKey() ) ) //
				.forEach( attribute -> filteredAttributes.put( attribute.getKey(), (String) attribute.getValue() ) );

		return filteredAttributes.immutable();
	}

	private Element filter( final Element element, final Element parent ) {
		final IdentifyingAttributes newIdentifyingAttributes = filter( element, element.getIdentifyingAttributes() );
		final Attributes newAttributes = filter( element, element.getAttributes() );
		final String retestId = element.getRetestId();
		final List<Element> containedElements = element.getContainedElements();

		final Element newElement = Element.create( retestId, parent, newIdentifyingAttributes, newAttributes );

		final List<Element> newContainedElements = filterContainedElements( newElement, containedElements );
		newElement.addChildren( newContainedElements );
		return newElement;
	}

	private List<Element> filterContainedElements( final Element newParent, final List<Element> containedElements ) {
		return containedElements.stream() //
				.map( element -> filter( element, newParent ) ) //
				.collect( Collectors.toList() );
	}

	private boolean filterNothing() {
		return filter == Filter.FILTER_NOTHING;
	}
}
