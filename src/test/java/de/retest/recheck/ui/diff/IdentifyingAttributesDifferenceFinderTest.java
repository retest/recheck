package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import de.retest.recheck.XmlTransformerUtil;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.DefaultAttribute;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.util.ApprovalsUtil;

public class IdentifyingAttributesDifferenceFinderTest {

	private static class Type {}

	private static class AnotherType {}

	private IdentifyingAttributes origin;
	private IdentifyingAttributes different;
	private IdentifyingAttributes differentOnlyPath;
	private IdentifyingAttributes differentOnlyVisible;

	private IdentifyingAttributesDifferenceFinder cut;

	@Before
	public void setUp() {
		origin = IdentifyingAttributes.create( Path.fromString( "parentPath/type[1]" ), Type.class );
		different = IdentifyingAttributes.create( Path.fromString( "anotherParentPath/anotherType[1]" ),
				AnotherType.class );
		differentOnlyPath = IdentifyingAttributes.create( Path.fromString( "parentPath/anotherType[1]" ), Type.class );
		differentOnlyVisible = IdentifyingAttributes.create( Path.fromString( "parentPath/type[1]" ), Type.class );

		cut = new IdentifyingAttributesDifferenceFinder();
	}

	@Test
	public void visible_attributes_should_produce_no_difference() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, differentOnlyVisible );

		assertThat( diff ).isNull();
	}

	@Test
	public void attributes_with_weight_zero_should_produce_no_difference() throws Exception {
		final String key = "key";

		final Attribute attribute1 = new DefaultAttribute( key, "value1" ) {
			private static final long serialVersionUID = 1L;

			@Override
			public double getWeight() {
				return Attribute.IGNORE_WEIGHT;
			}
		};
		final IdentifyingAttributes expected = mock( IdentifyingAttributes.class );
		when( expected.getAttributes() ).thenReturn( Collections.singletonList( attribute1 ) );

		final Attribute attribute2 = new DefaultAttribute( key, "value2" ) {
			private static final long serialVersionUID = 1L;

			@Override
			public double getWeight() {
				return Attribute.IGNORE_WEIGHT;
			}
		};
		final IdentifyingAttributes actual = mock( IdentifyingAttributes.class );
		when( actual.getAttributes() ).thenReturn( Collections.singletonList( attribute2 ) );

		when( actual.get( key ) ).thenReturn( attribute2.getValue() );

		final IdentifyingAttributesDifference diff = cut.differenceFor( expected, actual );

		assertThat( diff ).isNull();
	}

	@Test
	public void path_differences_should_only_be_accounted_for_topmost_elements_iff_there_is_no_type_difference() {
		IdentifyingAttributes expected = IdentifyingAttributes.create( Path.fromString( "a/b/c/d/e[1]" ), Type.class );
		IdentifyingAttributes actual = IdentifyingAttributes.create( Path.fromString( "A/b/c/d/e[1]" ), Type.class );
		IdentifyingAttributesDifference diff = cut.differenceFor( expected, actual );

		assertThat( diff ).isNull();

		expected = IdentifyingAttributes.create( Path.fromString( "a/b/c/d/e[1]" ), Type.class );
		actual = IdentifyingAttributes.create( Path.fromString( "a/b/C/d/e[1]" ), Type.class );
		diff = cut.differenceFor( expected, actual );

		assertThat( diff ).isNull();

		actual = IdentifyingAttributes.create( Path.fromString( "a/b/c/d/E[1]" ), AnotherType.class );
		diff = cut.differenceFor( expected, actual );

		assertThat( diff ).isNotNull();
		assertThat( diff.getAttributeDifferences().size() ).isEqualTo( 2 );

		expected = IdentifyingAttributes.create( Path.fromString( "a/b/c/d/e[1]" ), Type.class );
		actual = IdentifyingAttributes.create( Path.fromString( "a/b/c/d/E[1]" ), Type.class );
		diff = cut.differenceFor( expected, actual );

		assertThat( diff ).isNotNull();
		assertThat( diff.getAttributeDifferences().size() ).isEqualTo( 1 );
	}

	@Test
	public void differences_should_be_recognized_accordingly() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, different );

		assertThat( diff.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( diff.getAttributeDifferences().size() ).isEqualTo( 2 );
		assertThat( diff.getNonEmptyDifferences().size() ).isEqualTo( 0 );
		assertThat( diff.size() ).isEqualTo( 1 );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( diff ) );
	}

	@Test
	public void different_paths_and_components_should_be_recognized_accordingly() throws Exception {
		final IdentifyingAttributes expected = IdentifyingAttributes
				.create( Path.fromString( AnotherType.class.getSimpleName() + "[1]" ), AnotherType.class );
		final IdentifyingAttributes actual =
				IdentifyingAttributes.create( Path.fromString( Type.class.getSimpleName() + "[1]" ), Type.class );

		final IdentifyingAttributesDifference diff = cut.differenceFor( expected, actual );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( diff ) );
	}

	@Test
	public void toString_should_work_correctly_for_path_differences() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, differentOnlyPath );

		assertThat( diff.toString() )
				.isEqualTo( "expected path: parentPath[1]/type[1] - actual path: parentPath[1]/anotherType[1]" );
	}

	@Test
	public void toString_should_work_correctly_for_multiple_differences() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, different );

		assertThat( diff.toString() ).isEqualTo(
				"expected path: parentPath[1]/type[1] expected type: de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$Type - actual path: anotherParentPath[1]/anotherType[1] actual type: de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$AnotherType" );
	}

	@Test
	public void expected_and_actual_strings_should_be_correct() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, different );

		assertThat( diff.getExpected() ).isEqualTo(
				"path=parentPath[1]/type[1] type=de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$Type" );
		assertThat( diff.getActual() ).isEqualTo(
				"path=anotherParentPath[1]/anotherType[1] type=de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$AnotherType" );
	}

	@Test( expected = NullPointerException.class )
	public void exception_should_be_thrown_if_expected_is_null() {
		cut.differenceFor( null, origin );
	}

	@Test( expected = NullPointerException.class )
	public void exception_should_be_thrown_if_actual_is_null() {
		cut.differenceFor( origin, null );
	}

}
