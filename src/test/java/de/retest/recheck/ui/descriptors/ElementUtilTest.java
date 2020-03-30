package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.retest.recheck.ui.Path;

public class ElementUtilTest {

	@Test
	public void equal_paths_should_be_equal() throws Exception {
		final Path path = Path.fromString( "/some/path" );
		final IdentifyingAttributes identAttributes = mock( IdentifyingAttributes.class );
		when( identAttributes.getPathTyped() ).thenReturn( path );
		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identAttributes );

		assertThat( ElementUtil.pathEquals( element, element ) ).isTrue();
	}

	@Test
	public void unequal_paths_should_be_unequal() throws Exception {
		final Path path0 = Path.fromString( "/some/path" );
		final IdentifyingAttributes identAttributes0 = mock( IdentifyingAttributes.class );
		when( identAttributes0.getPathTyped() ).thenReturn( path0 );
		final Element element0 = mock( Element.class );
		when( element0.getIdentifyingAttributes() ).thenReturn( identAttributes0 );

		final Path path1 = Path.fromString( "/some/other/path" );
		final IdentifyingAttributes identAttributes1 = mock( IdentifyingAttributes.class );
		when( identAttributes1.getPathTyped() ).thenReturn( path1 );
		final Element element1 = mock( Element.class );
		when( element1.getIdentifyingAttributes() ).thenReturn( identAttributes1 );

		assertThat( ElementUtil.pathEquals( element0, element1 ) ).isFalse();
	}

	@Test
	public void child_elements_should_be_flattened_in_order() throws Exception {
		final Element grandchild = mock( Element.class );
		final List<Element> containendInChild1 = Arrays.asList( grandchild );

		final Element child0 = mock( Element.class );
		final Element child1 = mock( Element.class );
		when( child1.getContainedElements() ).thenReturn( containendInChild1 );
		final List<Element> containendInParent = Arrays.asList( child0, child1 );

		final Element parent = mock( Element.class );
		when( parent.getContainedElements() ).thenReturn( containendInParent );

		final List<Element> flattened = ElementUtil.flattenChildElements( parent );

		assertThat( flattened ).containsExactly( child0, child1, grandchild );
	}

	@Test
	public void all_elements_should_be_flattened_in_order() throws Exception {
		final Element parent0Grandchild = mock( Element.class );
		final List<Element> containendInParent0Child1 = Arrays.asList( parent0Grandchild );

		final Element parent0Child0 = mock( Element.class );
		final Element parent0Child1 = mock( Element.class );
		when( parent0Child1.getContainedElements() ).thenReturn( containendInParent0Child1 );
		final List<Element> containendInParent0 = Arrays.asList( parent0Child0, parent0Child1 );

		final Element parent0 = mock( Element.class );
		when( parent0.getContainedElements() ).thenReturn( containendInParent0 );

		final Element parent1Child = mock( Element.class );
		final List<Element> containendInParent1 = Arrays.asList( parent1Child );

		final Element parent1 = mock( Element.class );
		when( parent1.getContainedElements() ).thenReturn( containendInParent1 );

		final List<Element> flattened = ElementUtil.flattenAllElements( Arrays.asList( parent0, parent1 ) );

		assertThat( flattened ).containsExactly( parent0, parent0Child0, parent0Child1, parent0Grandchild, parent1,
				parent1Child );
	}

}
