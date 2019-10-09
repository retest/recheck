package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.Path.fromString;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.PathElement;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.TextAttribute;
import de.retest.recheck.ui.image.Screenshot;

public class ElementBuilder {

	public static Element buildElement() {
		final IdentifyingAttributes identifyingAttributes =
				new IdentifyingAttributes( createIdentifyingAttribute( null, comp1.class ) );
		final String retestId = "id";
		final Element parent = mock( Element.class );
		final Attributes attributes = toAttributes( "{color=blue}" );
		final Screenshot screenshot = null;

		final Element element = Element.create( retestId, parent, identifyingAttributes, attributes, screenshot );

		final Element child1 = Element.create( retestId, parent,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child1.class ) ),
				toAttributes( "{color=red}" ) );
		final Element child2 = Element.create( retestId, parent,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child2.class ) ),
				toAttributes( "{color=green}" ) );
		final Element child3 = Element.create( retestId, parent,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child3.class ) ),
				toAttributes( "{color=violett}" ) );

		final List<Element> containedComponents = Arrays.asList( child1, child2, child3 );
		element.addChildren( containedComponents );

		return element;
	}

	public static Attributes toAttributes( String attribute ) {
		final MutableAttributes result = new MutableAttributes();

		attribute = attribute.trim().substring( 1, attribute.length() - 1 );

		for ( final String criterion : attribute.split( "," ) ) {
			final String[] splitCriterion = criterion.trim().split( "=" );
			result.put( splitCriterion[0], splitCriterion[1] );
		}

		return result.immutable();
	}

	public static Collection<Attribute> createIdentifyingAttribute( final Path path, final Class<?> name ) {
		final Collection<Attribute> idAttributes = IdentifyingAttributes
				.createList( Path.path( path, new PathElement( name.getSimpleName(), 1 ) ), name.getName() );

		idAttributes.add( new StringAttribute( "name", null ) );
		idAttributes.add( new TextAttribute( "text", null ) );
		idAttributes.add( new TextAttribute( "id", name.getSimpleName() ) );

		return idAttributes;
	}

	public static class comp1 {}

	public static class child1 {}

	public static class child2 {}

	public static class child3 {}
}
