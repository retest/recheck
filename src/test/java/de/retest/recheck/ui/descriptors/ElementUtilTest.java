package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;

class ElementUtilTest {

	@Test
	void test_path_equality() throws Exception {
		final Element element0 = mock( Element.class, RETURNS_DEEP_STUBS );
		when( element0.getIdentifyingAttributes().getPathTyped() ).thenReturn( Path.fromString( "/some/path" ) );

		assertThat( ElementUtil.pathEquals( element0, element0 ) ).isTrue();

		final Element element1 = mock( Element.class, RETURNS_DEEP_STUBS );
		when( element1.getIdentifyingAttributes().getPathTyped() ).thenReturn( Path.fromString( "/some/other/path" ) );

		assertThat( ElementUtil.pathEquals( element0, element1 ) ).isFalse();
	}

	@Test
	void child_elements_should_be_flattened_in_order() throws Exception {
		final Element grandchild = mock( Element.class );
		final Element child0 = mock( Element.class );
		final Element child1 = mock( Element.class );
		when( child1.getContainedElements() ).thenReturn( Collections.singletonList( grandchild ) );
		final Element parent = mock( Element.class );
		when( parent.getContainedElements() ).thenReturn( Arrays.asList( child0, child1 ) );

		final List<Element> flattened = ElementUtil.flattenChildElements( parent );

		assertThat( flattened ).containsExactly( child0, child1, grandchild );
	}

	@Test
	void all_elements_should_be_flattened_in_order() throws Exception {
		final Element parent0Grandchild = mock( Element.class );
		final Element parent0Child0 = mock( Element.class );
		final Element parent0Child1 = mock( Element.class );
		when( parent0Child1.getContainedElements() ).thenReturn( Collections.singletonList( parent0Grandchild ) );
		final Element parent0 = mock( Element.class );
		when( parent0.getContainedElements() ).thenReturn( Arrays.asList( parent0Child0, parent0Child1 ) );
		final Element parent1Child = mock( Element.class );
		final Element parent1 = mock( Element.class );
		when( parent1.getContainedElements() ).thenReturn( Collections.singletonList( parent1Child ) );

		final List<Element> flattened = ElementUtil.flattenAllElements( Arrays.asList( parent0, parent1 ) );

		assertThat( flattened ).containsExactly( parent0, parent0Child0, parent0Child1, parent0Grandchild, parent1,
				parent1Child );
	}

}
