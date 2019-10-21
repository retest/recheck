package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.image.Screenshot.ImageType;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.AttributeChanges;
import de.retest.recheck.ui.review.ScreenshotChanges;

class RootElementTest {

	private static class Window {}

	private static class Comp {}

	private final IdentifyingAttributes windowIdentAttributes = RootIdentifyingAttributes
			.create( Path.fromString( "Window[1]" ), Window.class, "name", "Window Title A", "code-loc A" );
	private final IdentifyingAttributes compIdentAttributes = RootIdentifyingAttributes
			.create( Path.fromString( "Window[1]/Comp[1]" ), Comp.class, "name", "comp 1", "code-loc A" );
	private final IdentifyingAttributes childIdentAttributes0 = RootIdentifyingAttributes
			.create( Path.fromString( "Window[1]/Comp[1]/Comp[1]" ), Comp.class, "name", "child 1", "code-loc A" );
	private final Screenshot screenshot = new Screenshot( "", new byte[0], ImageType.PNG );

	@Test
	void applyChanges_should_add_inserted_components() throws Exception {
		final RootElement rootElement = rootElementFor( windowIdentAttributes, new Attributes(), screenshot );
		final Element child = Element.create( "wesdf", rootElement, compIdentAttributes, new Attributes() );

		final Element child1 = Element.create( "asdasg", child, childIdentAttributes0, new Attributes() );
		child.addChildren( child1 );

		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( child );

		final RootElement changed = rootElement.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();

		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( child );
	}

	@Test
	void applyChanges_should_remove_deleted_components() throws Exception {

		final RootElement rootElement = rootElementFor( windowIdentAttributes, new Attributes(), screenshot );

		final Element element = Element.create( "fsdfasd", rootElement, compIdentAttributes, new Attributes() );
		rootElement.addChildren( element );
		element.addChildren( Element.create( "asdas", element, childIdentAttributes0, new Attributes() ) );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( element.getIdentifyingAttributes() );

		final RootElement changed = rootElement.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	@Test
	void applyChanges_should_update_screenshot() throws Exception {
		final Screenshot newScreenshot = mock( Screenshot.class );

		final ScreenshotChanges screenshotChange = mock( ScreenshotChanges.class );
		when( screenshotChange.getScreenshot( any() ) ).thenReturn( newScreenshot );

		final IdentifyingAttributes identifyingAttribute = mock( IdentifyingAttributes.class );
		when( identifyingAttribute.applyChanges( any() ) ).thenReturn( identifyingAttribute );

		final Attributes attribute = mock( Attributes.class );
		when( attribute.applyChanges( any() ) ).thenReturn( attribute );

		final RootElement rootElement =
				new RootElement( "a", identifyingAttribute, attribute, screenshot, null, 0, null );

		final ActionChangeSet actionChangeSet = mock( ActionChangeSet.class );
		when( actionChangeSet.getIdentAttributeChanges() ).thenReturn( mock( AttributeChanges.class ) );
		when( actionChangeSet.getAttributesChanges() ).thenReturn( mock( AttributeChanges.class ) );
		when( actionChangeSet.getScreenshot() ).thenReturn( screenshotChange );

		final Screenshot screenshot = rootElement.getScreenshot();
		final RootElement changed = rootElement.applyChanges( actionChangeSet );

		assertThat( changed.getScreenshot() ).isNotEqualTo( screenshot );
		assertThat( changed.getScreenshot() ).isEqualTo( newScreenshot );
	}

	@Test
	public void parent_should_be_null() throws Exception {
		final String retestId = "someRetestId";
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		final Attributes attributes = mock( Attributes.class );
		final int screenId = 0;
		final RootElement cut =
				new RootElement( retestId, identifyingAttributes, attributes, null, null, screenId, null );
		assertThat( cut.getParent() ).isNull();
	}

	private RootElement rootElementFor( final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Screenshot screenshot, final Element... childrenArray ) {
		final RootElement rootElement = new RootElement( "asdasd", identifyingAttributes, attributes, screenshot,
				(String) identifyingAttributes.get( "name" ), identifyingAttributes.get( "name" ).hashCode(),
				identifyingAttributes.get( "text" ) + "-Window" );

		if ( childrenArray != null ) {
			rootElement.addChildren( childrenArray );
		}

		return rootElement;
	}

	private static class RootIdentifyingAttributes extends IdentifyingAttributes {

		private static final long serialVersionUID = 1L;

		public static IdentifyingAttributes create( final Path path, final Class<?> type, final String name,
				final String text, final String codeLoc ) {
			final Collection<Attribute> parent = IdentifyingAttributes.createList( path, type.getName() );
			parent.add( new StringAttribute( "name", name ) );
			return new RootIdentifyingAttributes( parent );
		}

		public RootIdentifyingAttributes( final Collection<Attribute> attributes ) {
			super( attributes );
		}
	}

}
