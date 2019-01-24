package de.retest.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import de.retest.ui.Path;
import de.retest.ui.image.Screenshot;
import de.retest.ui.image.Screenshot.ImageType;
import de.retest.ui.review.ActionChangeSet;

public class RootElementTest {

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
	public void applyChanges_adds_inserted_components() throws Exception {
		final RootElement rootElement = descriptorFor( windowIdentAttributes, new Attributes(), screenshot );
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
	public void applyChanges_removes_deleted_components() throws Exception {

		final RootElement rootElement = descriptorFor( windowIdentAttributes, new Attributes(), screenshot );

		final Element element = Element.create( "fsdfasd", rootElement, compIdentAttributes, new Attributes() );
		rootElement.addChildren( element );
		element.addChildren( Element.create( "asdas", element, childIdentAttributes0, new Attributes() ) );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( element.getIdentifyingAttributes() );

		final RootElement changed = rootElement.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	private RootElement descriptorFor( final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Screenshot screenshot, final Element... childrenArray ) {
		final List<Element> children = new ArrayList<>();

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

		@SuppressWarnings( "unused" )
		public RootIdentifyingAttributes() {}

		public RootIdentifyingAttributes( final Collection<Attribute> attributes ) {
			super( attributes );
		}

	}

}
