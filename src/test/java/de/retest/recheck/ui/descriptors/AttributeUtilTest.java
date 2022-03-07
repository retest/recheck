package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifferenceFinder;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;

class AttributeUtilTest {
	private static final Rectangle EXPECTED = new Rectangle( 200, 100 );
	private static final Rectangle ACTUAL = new Rectangle( 200, 500 );

	private IdentifyingAttributesDifferenceFinder finder;

	@BeforeEach
	void setUp() {
		finder = new IdentifyingAttributesDifferenceFinder();
	}

	@Test
	void getActualOutline_should_return_actual_relative_outline() {
		final OutlineAttribute expectedAtt = OutlineAttribute.create( EXPECTED );
		final OutlineAttribute actualAtt = OutlineAttribute.create( ACTUAL );

		final IdentifyingAttributes expected = new IdentifyingAttributes( Collections.singletonList( expectedAtt ) );
		final IdentifyingAttributes actual = new IdentifyingAttributes( Collections.singletonList( actualAtt ) );

		final IdentifyingAttributesDifference identifyingAttributesDifference =
				finder.differenceFor( expected, actual );

		final Element element = Element.create( "id", mock( Element.class ), expected, new Attributes(), null );

		final ElementDifference difference = new ElementDifference( element, mock( AttributesDifference.class ),
				identifyingAttributesDifference, null, null, Collections.singletonList( null ) );

		assertThat( AttributeUtil.getActualOutline( difference ) ).isEqualTo( ACTUAL );
	}

	@Test
	void getActualOutline_should_return_expected_outline() {
		final OutlineAttribute expectedAtt = OutlineAttribute.create( EXPECTED );

		final IdentifyingAttributes expected = new IdentifyingAttributes( Collections.singletonList( expectedAtt ) );

		final Element element = Element.create( "id", mock( Element.class ), expected, new Attributes(), null );

		final ElementDifference difference = new ElementDifference( element, mock( AttributesDifference.class ), null,
				null, null, Collections.singletonList( null ) );

		final Rectangle actualAbsoluteOutline = AttributeUtil.getActualOutline( difference );
		assertThat( actualAbsoluteOutline ).isEqualTo( EXPECTED );
	}

	@Test
	void getActualAbsoluteOutline_should_return_actual_absolute_outline() {
		final OutlineAttribute expectedAtt = OutlineAttribute.createAbsolute( EXPECTED );
		final OutlineAttribute actualAtt = OutlineAttribute.createAbsolute( ACTUAL );

		final IdentifyingAttributes expected = new IdentifyingAttributes( Collections.singletonList( expectedAtt ) );
		final IdentifyingAttributes actual = new IdentifyingAttributes( Collections.singletonList( actualAtt ) );

		final IdentifyingAttributesDifference identifyingAttributesDifference =
				finder.differenceFor( expected, actual );

		final Element element = Element.create( "id", mock( Element.class ), expected, new Attributes(), null );

		final ElementDifference difference = new ElementDifference( element, mock( AttributesDifference.class ),
				identifyingAttributesDifference, null, null, Collections.singletonList( null ) );

		assertThat( AttributeUtil.getActualAbsoluteOutline( difference ) ).isEqualTo( ACTUAL );
	}

	@Test
	void getActualAbsoluteOutline_should_return_expected_outline() {
		final OutlineAttribute expectedAtt = OutlineAttribute.createAbsolute( EXPECTED );

		final IdentifyingAttributes expected = new IdentifyingAttributes( Collections.singletonList( expectedAtt ) );

		final Element element = Element.create( "id", mock( Element.class ), expected, new Attributes(), null );

		final ElementDifference difference = new ElementDifference( element, mock( AttributesDifference.class ), null,
				null, null, Collections.singletonList( null ) );

		assertThat( AttributeUtil.getActualAbsoluteOutline( difference ) ).isEqualTo( EXPECTED );
	}

	@Test
	void getActualOutline_should_be_robust() {
		final ElementDifference difference = mock( ElementDifference.class, RETURNS_DEEP_STUBS );
		when( difference.getIdentifyingAttributesDifference() )
				.thenReturn( mock( InsertedDeletedElementDifference.class, RETURNS_DEEP_STUBS ) );
		assertThatCode( () -> AttributeUtil.getActualOutline( difference, "type" ) ).doesNotThrowAnyException();
	}
}
