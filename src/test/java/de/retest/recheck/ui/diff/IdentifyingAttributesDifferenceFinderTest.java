package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.XmlTransformerUtil;
import de.retest.recheck.ignore.GloballyIgnoredAttributes;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.AdditionalAttributeDifference;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.DefaultAttribute;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.util.ApprovalsUtil;

class IdentifyingAttributesDifferenceFinderTest {

	private static class Type {}

	private static class AnotherType {}

	private IdentifyingAttributes origin;
	private IdentifyingAttributes different;
	private IdentifyingAttributes differentOnlyPath;
	private IdentifyingAttributes differentOnlyVisible;

	private IdentifyingAttributesDifferenceFinder cut;

	@BeforeEach
	void setUp() {
		origin = IdentifyingAttributes.create( Path.fromString( "parentPath/type[1]" ), Type.class );
		different = IdentifyingAttributes.create( Path.fromString( "anotherParentPath/anotherType[1]" ),
				AnotherType.class );
		differentOnlyPath = IdentifyingAttributes.create( Path.fromString( "parentPath/anotherType[1]" ), Type.class );
		differentOnlyVisible = IdentifyingAttributes.create( Path.fromString( "parentPath/type[1]" ), Type.class );

		cut = new IdentifyingAttributesDifferenceFinder();
	}

	@Test
	void visible_attributes_should_produce_no_difference() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, differentOnlyVisible );

		assertThat( diff ).isNull();
	}

	@Test
	void ignored_attribute_should_produce_no_difference() throws Exception {
		final Collection<String> ignoredAttributes = Arrays.asList( IdentifyingAttributes.PATH_ATTRIBUTE_KEY );
		final GloballyIgnoredAttributes ignored = GloballyIgnoredAttributes.getTestInstance( ignoredAttributes );
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, differentOnlyPath, ignored );

		assertThat( diff ).isNull();
		GloballyIgnoredAttributes.getTestInstance();
	}

	@Test
	void new_identifying_attributes_should_produce_difference() throws Exception {
		final IdentifyingAttributes expectedIdentAttributes = new IdentifyingAttributes( Collections.emptyList() );

		final String newKey = "key";
		final Attribute newIdentifyingAttribute = new DefaultAttribute( newKey, "value" );
		final IdentifyingAttributes actualIdentAttributes =
				new IdentifyingAttributes( Arrays.asList( newIdentifyingAttribute ) );

		final IdentifyingAttributesDifference actualDiff =
				cut.differenceFor( expectedIdentAttributes, actualIdentAttributes );

		final IdentifyingAttributesDifference expectedDiff =
				new IdentifyingAttributesDifference( expectedIdentAttributes,
						Arrays.asList( new AdditionalAttributeDifference( newKey, newIdentifyingAttribute ) ) );
		assertThat( actualDiff ).isEqualTo( expectedDiff );
	}

	@Test
	void identifying_attributes_that_are_null_should_produce_no_difference() throws Exception {
		final Attribute attribute = new DefaultAttribute( "key", null );
		final IdentifyingAttributes expectedIdentAttributes = new IdentifyingAttributes( Arrays.asList( attribute ) );
		final IdentifyingAttributes actualIdentAttributes = new IdentifyingAttributes( Arrays.asList( attribute ) );

		final IdentifyingAttributesDifference diff =
				cut.differenceFor( expectedIdentAttributes, actualIdentAttributes );

		assertThat( diff ).isNull();
	}

	@Test
	void attributes_with_ignore_weight_should_produce_difference() throws Exception {
		final String key = "key";
		final String expectedValue = "value1";
		final String actualValue = "value2";

		final Attribute attribute1 = new DefaultAttribute( key, expectedValue ) {
			private static final long serialVersionUID = 1L;

			@Override
			public double getWeight() {
				return Attribute.IGNORE_WEIGHT;
			}
		};
		final IdentifyingAttributes expectedIdentAttributes = new IdentifyingAttributes( Arrays.asList( attribute1 ) );

		final Attribute attribute2 = new DefaultAttribute( key, actualValue ) {
			private static final long serialVersionUID = 1L;

			@Override
			public double getWeight() {
				return Attribute.IGNORE_WEIGHT;
			}
		};
		final IdentifyingAttributes actualIdentAttributes = new IdentifyingAttributes( Arrays.asList( attribute2 ) );

		final IdentifyingAttributesDifference actualDiff =
				cut.differenceFor( expectedIdentAttributes, actualIdentAttributes );
		final IdentifyingAttributesDifference expectedDiff = new IdentifyingAttributesDifference(
				expectedIdentAttributes, Arrays.asList( new AttributeDifference( key, expectedValue, actualValue ) ) );
		assertThat( actualDiff ).isEqualTo( expectedDiff );
	}

	@Test
	void path_differences_should_only_be_accounted_for_topmost_elements_iff_there_is_no_type_difference() {
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
	void differences_should_be_recognized_accordingly() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, different );

		assertThat( diff.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( diff.getAttributeDifferences().size() ).isEqualTo( 2 );
		assertThat( diff.getNonEmptyDifferences().size() ).isEqualTo( 0 );
		assertThat( diff.size() ).isEqualTo( 1 );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( diff ) );
	}

	@Test
	void different_paths_and_components_should_be_recognized_accordingly() throws Exception {
		final IdentifyingAttributes expected = IdentifyingAttributes
				.create( Path.fromString( AnotherType.class.getSimpleName() + "[1]" ), AnotherType.class );
		final IdentifyingAttributes actual =
				IdentifyingAttributes.create( Path.fromString( Type.class.getSimpleName() + "[1]" ), Type.class );

		final IdentifyingAttributesDifference diff = cut.differenceFor( expected, actual );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( diff ) );
	}

	@Test
	void toString_should_work_correctly_for_path_differences() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, differentOnlyPath );

		assertThat( diff.toString() )
				.isEqualTo( "expected path: parentPath[1]/type[1] - actual path: parentPath[1]/anotherType[1]" );
	}

	@Test
	void toString_should_work_correctly_for_multiple_differences() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, different );

		assertThat( diff.toString() ).isEqualTo(
				"expected path: parentPath[1]/type[1] expected type: de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$Type - actual path: anotherParentPath[1]/anotherType[1] actual type: de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$AnotherType" );
	}

	@Test
	void expected_and_actual_strings_should_be_correct() {
		final IdentifyingAttributesDifference diff = cut.differenceFor( origin, different );

		assertThat( diff.getExpected() ).isEqualTo(
				"path=parentPath[1]/type[1] type=de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$Type" );
		assertThat( diff.getActual() ).isEqualTo(
				"path=anotherParentPath[1]/anotherType[1] type=de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinderTest$AnotherType" );
	}

	@Test
	void exception_should_be_thrown_if_expected_is_null() {
		assertThatThrownBy( () -> cut.differenceFor( null, origin ) ).isExactlyInstanceOf( NullPointerException.class );
	}

	@Test
	void exception_should_be_thrown_if_actual_is_null() {
		assertThatThrownBy( () -> cut.differenceFor( origin, null ) ).isExactlyInstanceOf( NullPointerException.class );
	}

}
