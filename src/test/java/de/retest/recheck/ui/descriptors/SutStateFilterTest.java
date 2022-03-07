package de.retest.recheck.ui.descriptors;

import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.meta.MetadataProvider;
import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.image.Screenshot.ImageType;

class SutStateFilterTest {

	static class AttributeFilterTests {
		Element element;
		Attributes attributes;

		@BeforeEach
		void setUp() {
			element = mock( Element.class );
			final MutableAttributes mutable = new MutableAttributes();
			mutable.put( "enabled", "true" );
			mutable.put( "rowCount", "5" );
			attributes = mutable.immutable();
		}

		@Test
		void should_return_same_attributes_with_filter_nothing() {
			final Filter noFilter = Filter.NEVER_MATCH;
			final SutStateFilter cut = new SutStateFilter( noFilter );
			final Attributes newState = cut.filter( element, attributes );

			assertThat( (Object) newState ).isEqualTo( attributes );
		}

		@Test
		void should_return_same_attributes_with_no_filter() {
			final Filter noFilter = element -> false;
			final SutStateFilter cut = new SutStateFilter( noFilter );
			final Attributes newState = cut.filter( element, attributes );

			assertThat( (Object) newState ).isEqualTo( attributes );
		}

		@Test
		void should_filter_respective_attribute() {
			final Filter filter = new AttributeFilter( Attributes.ENABLED );
			final SutStateFilter cut = new SutStateFilter( filter );
			final Attributes newState = cut.filter( element, attributes );

			assertThat( newState.size() ).isEqualTo( 1 );
			assertThat( newState.get( "enabled" ) ).isNull();
			assertThat( newState.get( "rowCount" ) ).isEqualTo( "5" );
		}

		@Test
		void should_not_do_anything_with_no_matches() {
			final Filter filter = new AttributeFilter( Attributes.FONT_FAMILY );
			final SutStateFilter cut = new SutStateFilter( filter );
			final Attributes newState = cut.filter( element, attributes );

			assertThat( (Object) newState ).isEqualTo( attributes );
		}

	}

	static class IdentifyingAttributesFilterTests {
		Element element;
		IdentifyingAttributes identifyingAttributes;

		@BeforeEach
		void setUp() {
			final Path path = Path.fromString( "Window[1]/Path[1]/Component[1]" );
			element = mock( Element.class );
			identifyingAttributes = IdentifyingAttributes.create( path, Component.class );
		}

		@Test
		void should_return_same_identifying_attributes_with_filter_nothing() {
			final Filter filter = Filter.NEVER_MATCH;
			final SutStateFilter cut = new SutStateFilter( filter );
			final IdentifyingAttributes filteredIdentifyingAttributes = cut.filter( element, identifyingAttributes );

			assertThat( filteredIdentifyingAttributes ).isEqualTo( identifyingAttributes );
		}

		@Test
		void should_return_same_identifying_attributes_with_no_filter() {
			final Filter noFilter = element -> false;
			final SutStateFilter cut = new SutStateFilter( noFilter );
			final IdentifyingAttributes filteredIdentifyingAttributes = cut.filter( element, identifyingAttributes );

			assertThat( filteredIdentifyingAttributes ).isEqualTo( identifyingAttributes );
		}

		@Test
		void should_filter_respective_attribute() {
			final Filter filter = new AttributeFilter( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final SutStateFilter cut = new SutStateFilter( filter );
			final IdentifyingAttributes filteredIdentifyingAttributes = cut.filter( element, identifyingAttributes );

			final String filteredIdentifyingAttributesPath =
					filteredIdentifyingAttributes.get( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final String filteredIdentifyingAttributesType = filteredIdentifyingAttributes.getType();

			assertThat( filteredIdentifyingAttributesPath ).isNull();
			assertThat( filteredIdentifyingAttributesType ).isEqualTo( identifyingAttributes.getType() );
		}

		@Test
		void should_not_do_anything_with_no_matches() {
			final Filter filter = new CompoundFilter( new AttributeFilter( "text" ) );
			final SutStateFilter cut = new SutStateFilter( filter );
			final IdentifyingAttributes filteredIdentifyingAttributes = cut.filter( element, identifyingAttributes );

			assertThat( filteredIdentifyingAttributes ).isEqualTo( identifyingAttributes );
		}
	}

	@Nested
	class RootElementFilterTests {
		RootElement rootElement;
		Element childElement;
		Element grandChildElement;

		@BeforeEach
		void setUp() {
			// Window[1]             = rootElement
			//   |- Path[1]        	 = child
			//     |- Component[1]   = grandChild
			final Screenshot screenshot = new Screenshot( "", new byte[0], ImageType.PNG );
			rootElement = createRootElement( "Window[1]", Window.class, screenshot, "name", "Window Title A" );
			childElement = createElement( "Window[1]/Path[1]", Component.class, rootElement );
			grandChildElement = createElement( "Window[1]/Path[1]/Component[1]", Component.class, childElement );
			rootElement.addChildren( childElement );
			childElement.addChildren( grandChildElement );
		}

		@Test
		void should_propagate_to_child_components() {
			final Filter filter = new AttributeFilter( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final SutStateFilter cut = new SutStateFilter( filter );
			final Element filteredRootElement = cut.filter( rootElement );

			final String filteredRootElementPath =
					filteredRootElement.getIdentifyingAttributes().get( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final String filteredRootElementType = filteredRootElement.getIdentifyingAttributes().getType();
			final List<Element> filteredRootElementContainedElements = filteredRootElement.getContainedElements();
			assertThat( filteredRootElementContainedElements.size() ).isEqualTo( 1 );
			final Element filteredChild = filteredRootElementContainedElements.get( 0 );
			final String filteredChildPath =
					filteredChild.getIdentifyingAttributes().get( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			assertThat( filteredChild.getContainedElements().size() ).isEqualTo( 1 );
			final Element filteredGrandChild = filteredChild.getContainedElements().get( 0 );
			final String filteredGrandChildPath =
					filteredGrandChild.getIdentifyingAttributes().get( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );

			assertThat( filteredRootElementPath ).isNull();
			assertThat( filteredRootElementType ).isEqualTo( rootElement.getIdentifyingAttributes().getType() );
			assertThat( filteredChildPath ).isNull();
			assertThat( filteredChild.getIdentifyingAttributes().getType() )
					.isEqualTo( childElement.getIdentifyingAttributes().getType() );
			assertThat( filteredGrandChildPath ).isNull();
			assertThat( filteredGrandChild.getIdentifyingAttributes().getType() )
					.isEqualTo( grandChildElement.getIdentifyingAttributes().getType() );

			// Validate parents
			assertThat( filteredRootElement.getParent() ).isNull();
			assertThat( filteredChild.getParent() ).isSameAs( filteredRootElement );
			assertThat( filteredGrandChild.getParent() ).isSameAs( filteredChild );
			assertThat( filteredRootElement.getContainedElements() ).contains( filteredChild );
			assertThat( filteredChild.getContainedElements() ).contains( filteredGrandChild );
		}

		@Test
		void should_return_same_root_element_with_filter_nothing() {
			final Filter noFilter = Filter.NEVER_MATCH;
			final SutStateFilter cut = new SutStateFilter( noFilter );
			final Element filteredRootElement = cut.filter( rootElement );

			assertThat( filteredRootElement ).isSameAs( rootElement );
		}

		@Test
		void should_return_same_root_element_with_no_filter() {
			final Filter noFilter = element -> false;
			final SutStateFilter cut = new SutStateFilter( noFilter );
			final Element filteredRootElement = cut.filter( rootElement );

			assertThat( filteredRootElement ).isEqualTo( rootElement );
		}
	}

	@Nested
	class SutStateFilterTests {
		SutState sutState;

		@BeforeEach
		void setUp() {
			final Screenshot screenshot = new Screenshot( "", new byte[0], ImageType.PNG );
			final RootElement rootElement =
					createRootElement( "Window[1]", Window.class, screenshot, "name", "Window Title A" );
			final Element childElement = createElement( "Window[1]/Path[1]", Component.class, rootElement );
			rootElement.addChildren( childElement );
			final List<RootElement> rootElements = Arrays.asList( rootElement );
			sutState = createSutState( rootElements );
		}

		@Test
		void should_filter_each_root_element() {
			final Filter filter = new AttributeFilter( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final SutStateFilter cut = new SutStateFilter( filter );
			final SutState filteredSutState = cut.filter( sutState );
			final RootElement filteredRootElement = filteredSutState.getRootElements().get( 0 );
			final Element filteredChildElement = filteredRootElement.getContainedElements().get( 0 );

			final String filteredRootElementPath =
					filteredRootElement.getIdentifyingAttributes().get( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final String filteredRootElementType = filteredRootElement.getIdentifyingAttributes().getType();
			final Element rootElement = sutState.getRootElements().get( 0 );
			final String filteredChildElementPath =
					filteredChildElement.getIdentifyingAttributes().get( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final String filteredChildElementType = filteredChildElement.getIdentifyingAttributes().getType();
			final Element childElement = rootElement.getContainedElements().get( 0 );

			assertThat( filteredSutState.getMetadata() ).isEqualTo( sutState.getMetadata() );
			assertThat( filteredRootElementPath ).isNull();
			assertThat( filteredRootElementType ).isEqualTo( rootElement.getIdentifyingAttributes().getType() );
			assertThat( filteredChildElementPath ).isNull();
			assertThat( filteredChildElementType ).isEqualTo( childElement.getIdentifyingAttributes().getType() );

			assertThat( filteredRootElement.getParent() ).isNull();
			assertThat( filteredChildElement.getParent() ).isSameAs( filteredRootElement );
		}

		@Test
		void should_return_same_sut_state_with_filter_nothing() {
			final Filter noFilter = Filter.NEVER_MATCH;
			final SutStateFilter cut = new SutStateFilter( noFilter );
			final SutState filteredSutState = cut.filter( sutState );

			assertThat( filteredSutState ).isSameAs( sutState );
		}

		@Test
		void should_return_same_sut_state_with_no_filter() {
			final Filter noFilter = element -> false;
			final SutStateFilter cut = new SutStateFilter( noFilter );
			final SutState filteredSutState = cut.filter( sutState );

			assertThat( filteredSutState ).isEqualTo( sutState );
		}

		@Test
		void should_return_same_sut_state_with_empty_sut_state() {
			final Filter filter = new AttributeFilter( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
			final SutStateFilter cut = new SutStateFilter( filter );
			final SutState emptyState = createSutState( Collections.emptyList() );
			final SutState filteredEmptyState = cut.filter( emptyState );

			assertThat( filteredEmptyState ).isEqualTo( emptyState );

		}
	}

	private Element createElement( final String path, final Class<?> type, final Element parent ) {
		return Element.create( "asdas", parent, IdentifyingAttributes.create( fromString( path ), type ),
				new Attributes() );
	}

	private RootElement createRootElement( final String path, final Class<?> type, final Screenshot screenshot,
			final String screen, final String title ) {
		return new RootElement( "asdas", IdentifyingAttributes.create( fromString( path ), type ), new Attributes(),
				screenshot, screen, screen.hashCode(), title );
	}

	private SutState createSutState( final Collection<RootElement> rootElements ) {
		return new SutState( rootElements, MetadataProvider.empty() );
	}

	private static class Component {}

	private static class Window {}

}
