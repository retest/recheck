package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.AdditionalAttributeDifference;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.ContextAttribute;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.diff.AttributeDifference;

public class IdentifyingAttributes_proprietary_Test {

	@Test
	public void more_zero_weight_actual_attributes_should_not_result_in_difference() throws Exception {
		final Collection<Attribute> attributes = IdentifyingAttributes
				.createList( Path.fromString( "Window[0]/path[0]/component[0]" ), component.class.getName() );
		final IdentifyingAttributes expected = new IdentifyingAttributes( attributes );
		attributes.add( new ContextAttribute( "some value" ) );
		final IdentifyingAttributes actual = new IdentifyingAttributes( attributes );

		assertThat( expected.match( actual ) ).isCloseTo( 1.0, within( 0.001 ) );
		assertThat( expected.hashCode() ).isEqualTo( actual.hashCode() );
		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	public void more_zero_weight_expected_attributes_should_not_result_in_difference() throws Exception {
		final Collection<Attribute> attributes = IdentifyingAttributes
				.createList( Path.fromString( "Window[0]/path[0]/component[0]" ), component.class.getName() );
		final IdentifyingAttributes actual = new IdentifyingAttributes( attributes );
		attributes.add( new ContextAttribute( "some value" ) );
		final IdentifyingAttributes expected = new IdentifyingAttributes( attributes );

		assertThat( expected.match( actual ) ).isCloseTo( 1.0, within( 0.001 ) );
		assertThat( expected.hashCode() ).isEqualTo( actual.hashCode() );
		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	public void apply_additional_attribute_should_add_attribute() {
		final IdentifyingAttributes original =
				IdentifyingAttributes.create( Path.fromString( "Window[0]/path[0]/component[0]" ), component.class );
		final String key = "newAttribute";
		final String value = "new value";
		final Set<AttributeDifference> attributeChanges = new HashSet<>(
				Arrays.asList( new AdditionalAttributeDifference( key, new StringAttribute( key, value ) ) ) );

		final IdentifyingAttributes changed = original.applyChanges( attributeChanges );

		assertThat( changed ).isNotEqualTo( original );
		assertThat( changed.getAttribute( key ).getValue() ).isEqualTo( value );
	}

	private static class component {}

}
