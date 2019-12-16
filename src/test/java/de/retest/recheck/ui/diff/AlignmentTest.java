package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.OutlineAttribute;
import de.retest.recheck.ui.descriptors.PathAttribute;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.SuffixAttribute;

class AlignmentTest {

	private static final class Root {}

	private static final class Comp {}

	private static final class OtherComp {}

	@Test
	void toIdentityMapping_should_still_work_if_optimized() {
		final Element expected = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Window[1]/Layer[1]/Layer[3]/Comp1[1]" ), Comp.class ),
				new Attributes() );
		final Element expectedClone = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Window[1]/Layer[1]/Layer[3]/Comp1[1]" ), Comp.class ),
				new Attributes() );
		final Map<Element, Element> mapping = Alignment.toIdentityMapping( Collections.singletonList( expected ) );

		final Element actual = mapping.get( expectedClone );

		assertThat( actual ).isSameAs( expected );
	}

	@Test
	void toIdentityMapping_should_raise_exception_on_duplicates() throws Exception {
		final Element e = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Comp[1]" ), Comp.class ), new Attributes() );
		assertThatThrownBy( () -> Alignment.toIdentityMapping( Arrays.asList( e, e ) ) )
				.isInstanceOf( IllegalStateException.class );
	}

	@Test
	void simple_one_on_one_alignment() throws Exception {
		// root/comp0 - root/comp0
		final Element expComp0 = buildEqual( "root[0]/comp[0]", Comp.class );
		final Element expected = buildEqual( "root[0]", Root.class, expComp0 );

		final Element actComp0 = buildEqual( "root[0]/comp[0]", Comp.class );
		final Element actual = buildEqual( "root[0]", Root.class, actComp0 );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.getActual( expComp0 ) ).isSameAs( actComp0 );
	}

	@Test
	void alignment_with_one_hierarchy_difference() throws Exception {
		// root/comp0 - root/comp0/comp0
		final Element expComp0 = buildEqual( "root[0]/comp[0]", Comp.class );
		final Element expected = buildEqual( "root[0]", Root.class, expComp0 );

		final Element actComp0Comp0 = buildEqual( "root[0]/comp[0]/comp[0]", Comp.class );
		final Element actComp0 = buildEqual( "root[0]/comp[0]", OtherComp.class, actComp0Comp0 );
		final Element actual = buildEqual( "root[0]", Root.class, actComp0 );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.getActual( expComp0 ) ).isSameAs( actComp0Comp0 );
	}

	@Test
	void alignment_with_added() throws Exception {
		// root/a/b - root/a/c, root/a/b/
		final Element expB = buildEqual( "root/a[0]/b[0]", Comp.class );
		final Element expA = buildEqual( "root/a[0]", Comp.class, expB );
		final Element expected = buildEqual( "root[0]", Root.class, expA );

		final Element actC = buildEqual( "root/a[0]/c[0]", OtherComp.class );
		final Element actB = buildEqual( "root/a[0]/b[0]", Comp.class );
		final Element actA = buildEqual( "root/a[0]", Comp.class, actC, actB );
		final Element actual = buildEqual( "root[0]", Root.class, actA );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.getActual( expB ) ).isSameAs( actB );
		assertThat( alignment.getActual( expA ) ).isSameAs( actA );
	}

	@Test
	void alignment_with_intermediate_changed() throws Exception {
		// root/a/a/b - root/a/b
		final Element expB = buildEqual( "root[0]/a[0]/a[0]/b[0]", Comp.class );
		final Element expA2 = buildEqual( "root[0]/a[0]/a[0]", Comp.class, expB );
		final Element expA1 = buildEqual( "root[0]/a[0]", Comp.class, expA2 );
		final Element expected = buildEqual( "root[0]", Root.class, expA1 );

		final Element actB = buildEqual( "root[0]/a[0]/b[0]", Comp.class );
		final Element actA = buildEqual( "root[0]/a[0]", Comp.class, actB );
		final Element actual = buildEqual( "root[0]", Root.class, actA );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.getActual( expB ) ).isSameAs( actB );
		assertThat( alignment.getActual( expA1 ) ).isSameAs( actA );
		assertThat( alignment.getActual( expA2 ) ).isNull();
	}

	@Test
	void alignment_with_more_parents() throws Exception {
		// root/a0/b, root/a0/c - root/a0/b, root/a1/c
		final Element expB = buildEqual( "root[0]/a[0]/b[0]", Comp.class );
		final Element expC = buildEqual( "root[0]/a[0]/c[0]", OtherComp.class );
		final Element expA = buildEqual( "root[0]/a[0]", Comp.class, expB, expC );
		final Element expected = buildEqual( "root[0]", Root.class, expA );

		final Element actB = buildEqual( "root[0]/a[0]/b[0]", Comp.class );
		final Element actC = buildEqual( "root[0]/a[1]/c[0]", OtherComp.class );
		final Element actA0 = buildEqual( "root[0]/a[0]", Comp.class, actB );
		final Element actA1 = buildEqual( "root[0]/a[1]", Comp.class, actC );
		final Element actual = buildEqual( "root[0]", Root.class, actA0, actA1 );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.getActual( expB ) ).isSameAs( actB );
		assertThat( alignment.getActual( expC ) ).isSameAs( actC );
	}

	@Test
	void alignment_with_less_parents() throws Exception {
		// root/a0/b, root/a1/c - root/a0/b, root/a0/c
		final Element expB = buildEqual( "root[0]/a[0]/b[0]", Comp.class );
		final Element expC = buildEqual( "root[0]/a[1]/c[0]", OtherComp.class );
		final Element expA0 = buildEqual( "root[0]/a[0]", Comp.class, expB );
		final Element expA1 = buildEqual( "root[0]/a[1]", Comp.class, expC );
		final Element expected = buildEqual( "root[0]", Root.class, expA0, expA1 );

		final Element actB = buildEqual( "root[0]/a[0]/b[0]", Comp.class );
		final Element actC = buildEqual( "root[0]/a[0]/c[0]", OtherComp.class );
		final Element actA = buildEqual( "root[0]/a[0]", Comp.class, actB, actC );
		final Element actual = buildEqual( "root[0]", Root.class, actA );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.getActual( expB ) ).isSameAs( actB );
		assertThat( alignment.getActual( expC ) ).isSameAs( actC );
		assertThat( alignment.getActual( expA0 ) ).isSameAs( actA );
	}

	@Test
	void very_different_elements_should_not_be_aligned() {
		final Element root = buildEqual( "html[1]", Root.class );

		final List<Attribute> identifying = new ArrayList<>();
		identifying.add( new StringAttribute( "id", "change-for-div" ) );
		identifying.add( new StringAttribute( "text", "Change" ) );
		identifying.add( new StringAttribute( IdentifyingAttributes.TYPE_ATTRIBUTE_KEY, "button" ) );
		identifying.add( new PathAttribute( Path.fromString( "html[1]/body[1]/button[1]" ) ) );
		identifying.add( new SuffixAttribute( 1 ) );
		identifying.add( OutlineAttribute.create( new Rectangle( 0, 237, -1121, -237 ) ) );
		identifying.add( OutlineAttribute.createAbsolute( new Rectangle( 8, 253, 63, 21 ) ) );

		final MutableAttributes attributes = new MutableAttributes();
		attributes.put( "shown", true );

		final Element expected =
				Element.create( "retestId", root, new IdentifyingAttributes( identifying ), attributes.immutable() );
		root.addChildren( expected );

		final Element actualRoot = buildEqual( "html[1]", Root.class );

		final List<Attribute> actualIdentifying = new ArrayList<>();
		identifying.add( new StringAttribute( "text",
				"file:///Users/roessler/Documents/startup/workspace/recheck-web/src/test/resources/pages/non-existent.html" ) );
		identifying.add( new StringAttribute( IdentifyingAttributes.TYPE_ATTRIBUTE_KEY, "title" ) );
		identifying.add( new PathAttribute( Path.fromString( "html[1]/head[1]/title[1]" ) ) );
		identifying.add( new SuffixAttribute( 1 ) );
		identifying.add( OutlineAttribute.create( new Rectangle( 0, 0, 0, 0 ) ) );
		identifying.add( OutlineAttribute.createAbsolute( new Rectangle( 0, 0, 0, 0 ) ) );

		final MutableAttributes actualAttributes = new MutableAttributes();
		attributes.put( "shown", false );

		final Element actual = Element.create( "otherRetestId", actualRoot,
				new IdentifyingAttributes( actualIdentifying ), actualAttributes.immutable() );
		actualRoot.addChildren( actual );

		final Alignment alignment = Alignment.createAlignment( root, actualRoot );
		final Element mappedActual = alignment.getActual( expected );

		assertThat( mappedActual ).isNotEqualTo( actual );
		assertThat( mappedActual ).isNull();
	}

	@Test
	void toReverseDeque_should_create_reverse_deque() throws Exception {
		final Element e0 = Element.create( "e0", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Element[1]" ), Comp.class ), new Attributes() );
		final Element e1 = Element.create( "e1", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Element[2]" ), Comp.class ), new Attributes() );
		final Element e2 = Element.create( "e2", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Element[3]" ), Comp.class ), new Attributes() );
		final List<Element> elements = Arrays.asList( e0, e1, e2 );

		final Deque<Element> deque = Alignment.toReverseDeque( elements );

		assertThat( deque ).containsExactly( e2, e1, e0 );
	}

	private static Element buildEqual( final String path, final Class<?> type, final Element... containedComponents ) {
		final Element element = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( fromString( path ), type ), new Attributes() );
		element.addChildren( containedComponents );
		return element;
	}
}
