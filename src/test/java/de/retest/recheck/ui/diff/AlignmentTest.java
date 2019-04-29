package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class AlignmentTest {

	private static final class Root {}

	private static final class Comp {}

	private static final class OtherComp {}

	@Test
	public void toMapping_should_still_work_if_optimized() {
		final Element expected = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Window[1]/Layer[1]/Layer[3]/Comp1[1]" ), Comp.class ),
				new Attributes() );
		final Element expectedClone = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Window[1]/Layer[1]/Layer[3]/Comp1[1]" ), Comp.class ),
				new Attributes() );
		final Map<Element, Element> mapping = Alignment.toMapping( Collections.singletonList( expected ) );

		final Element actual = mapping.get( expectedClone );

		assertThat( actual ).isSameAs( expected );
	}

	@Test
	public void simple_one_on_one_alignment() throws Exception {
		// root/comp0 - root/comp0
		final Element expComp0 = buildEqual( "root[0]/comp[0]", Comp.class );
		final Element expected = buildEqual( "root[0]", Root.class, expComp0 );

		final Element actComp0 = buildEqual( "root[0]/comp[0]", Comp.class );
		final Element actual = buildEqual( "root[0]", Root.class, actComp0 );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.get( expComp0 ) ).isSameAs( actComp0 );
	}

	@Test
	public void alignment_with_one_hierarchy_difference() throws Exception {
		// root/comp0 - root/comp0/comp0
		final Element expComp0 = buildEqual( "root[0]/comp[0]", Comp.class );
		final Element expected = buildEqual( "root[0]", Root.class, expComp0 );

		final Element actComp0Comp0 = buildEqual( "root[0]/comp[0]/comp[0]", Comp.class );
		final Element actComp0 = buildEqual( "root[0]/comp[0]", OtherComp.class, actComp0Comp0 );
		final Element actual = buildEqual( "root[0]", Root.class, actComp0 );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.get( expComp0 ) ).isSameAs( actComp0Comp0 );
	}

	@Test
	public void alignment_with_added() throws Exception {
		// root/a/b - root/a/c, root/a/b/
		final Element expB = buildEqual( "root/a[0]/b[0]", Comp.class );
		final Element expA = buildEqual( "root/a[0]", Comp.class, expB );
		final Element expected = buildEqual( "root[0]", Root.class, expA );

		final Element actC = buildEqual( "root/a[0]/c[0]", OtherComp.class );
		final Element actB = buildEqual( "root/a[0]/b[0]", Comp.class );
		final Element actA = buildEqual( "root/a[0]", Comp.class, actC, actB );
		final Element actual = buildEqual( "root[0]", Root.class, actA );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.get( expB ) ).isSameAs( actB );
		assertThat( alignment.get( expA ) ).isSameAs( actA );
	}

	@Test
	public void alignment_with_intermediate_changed() throws Exception {
		// root/a/a/b - root/a/b
		final Element expB = buildEqual( "root[0]/a[0]/a[0]/b[0]", Comp.class );
		final Element expA2 = buildEqual( "root[0]/a[0]/a[0]", Comp.class, expB );
		final Element expA1 = buildEqual( "root[0]/a[0]", Comp.class, expA2 );
		final Element expected = buildEqual( "root[0]", Root.class, expA1 );

		final Element actB = buildEqual( "root[0]/a[0]/b[0]", Comp.class );
		final Element actA = buildEqual( "root[0]/a[0]", Comp.class, actB );
		final Element actual = buildEqual( "root[0]", Root.class, actA );

		final Alignment alignment = Alignment.createAlignment( expected, actual );

		assertThat( alignment.get( expB ) ).isSameAs( actB );
		assertThat( alignment.get( expA1 ) ).isSameAs( actA );
		assertThat( alignment.get( expA2 ) ).isNull();
	}

	@Test
	public void alignment_with_more_parents() throws Exception {
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

		assertThat( alignment.get( expB ) ).isSameAs( actB );
		assertThat( alignment.get( expC ) ).isSameAs( actC );
	}

	@Test
	public void alignment_with_less_parents() throws Exception {
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

		assertThat( alignment.get( expB ) ).isSameAs( actB );
		assertThat( alignment.get( expC ) ).isSameAs( actC );
		assertThat( alignment.get( expA0 ) ).isSameAs( actA );
	}

	private static Element buildEqual( final String path, final Class<?> type, final Element... containedComponents ) {
		final Element element = Element.create( "id", new Element() {},
				IdentifyingAttributes.create( fromString( path ), type ), new Attributes() );
		element.addChildren( containedComponents );
		return element;
	}
}
