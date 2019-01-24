package de.retest.ui.descriptors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.image.Screenshot;
import de.retest.ui.image.Screenshot.ImageType;

public class AttributesTest {

	@Test( expected = UnsupportedOperationException.class )
	public void Attributes_is_unmodifiable() {
		final MutableAttributes state = new MutableAttributes();
		state.put( "enabled", "true" );
		state.put( "disabled", "false" );
		final Attributes immutable = state.immutable();
		final Iterator<Entry<String, Object>> iter = immutable.iterator();
		iter.next();
		iter.remove();
		assertThat( (Iterable<?>) immutable ).hasSize( 2 );
	}

	@Test
	public void not_apply_unknown_attribute() {
		final MutableAttributes state = new MutableAttributes();
		state.put( "enabled", "true" );
		final Attributes changed = state.immutable().applyChanges( createAttributeChanges( "hutzlebug", "", "" ) );
		assertThat( (Object) changed ).isEqualTo( state.immutable() );
	}

	@Test
	public void apply_attribute() {
		final MutableAttributes state = new MutableAttributes();
		state.put( "enabled", "true" );
		final Attributes changed =
				state.immutable().applyChanges( createAttributeChanges( "enabled", "true", "false" ) );
		assertThat( (Object) changed ).isNotEqualTo( state.immutable() );
		assertThat( changed.get( "enabled" ) ).isEqualTo( "false" );
	}

	@Test
	public void not_apply_attribute_with_different_former_value() {
		final MutableAttributes state = new MutableAttributes();
		state.put( "rowCount", "5" );
		final Attributes changed = state.immutable().applyChanges( createAttributeChanges( "rowCount", "4", "6" ) );
		assertThat( (Object) changed ).isEqualTo( state.immutable() );
		assertThat( state.get( "rowCount" ) ).isEqualTo( "5" );
	}

	@Test
	public void delete_attribute() {
		final MutableAttributes state = new MutableAttributes();
		state.put( "rowCount", "5" );
		final Attributes changed = state.immutable().applyChanges( createAttributeChanges( "rowCount", "5", null ) );
		assertThat( (Object) changed ).isNotEqualTo( state.immutable() );
		assertThat( changed.get( "rowCount" ) ).isNull();
	}

	@Test
	public void apply_add_new_attribute() {
		final MutableAttributes state = new MutableAttributes();
		final Attributes changed = state.immutable().applyChanges( createAttributeChanges( "rowCount", null, "5" ) );
		assertThat( (Object) changed ).isNotEqualTo( state.immutable() );
		assertThat( changed.get( "rowCount" ) ).isEqualTo( "5" );
	}

	@Test
	public void applyChanges_with_screenshot_should_update_screenshot() throws Exception {
		final MutableAttributes mutable = new MutableAttributes();
		final Screenshot oldScreenshot = new Screenshot( "test", new byte[] {}, ImageType.PNG );
		mutable.put( oldScreenshot );
		final Attributes state = mutable.immutable();

		assertThat( state.get( Attributes.SCREENSHOT ) ).isNotNull();
		assertThat( state.getMap().get( Attributes.SCREENSHOT ) ).isNotNull();

		final Screenshot newScreenshot = new Screenshot( "testNew", new byte[] {}, ImageType.PNG );
		final Attributes newState = state.applyChanges( new HashSet<>(
				asList( new AttributeDifference( Attributes.SCREENSHOT, oldScreenshot, newScreenshot ) ) ) );

		assertThat( newState.get( Attributes.SCREENSHOT ) ).isEqualTo( newScreenshot );
	}

	private Set<AttributeDifference> createAttributeChanges( final String key, final String expected,
			final String actual ) {
		return Collections.singleton( new AttributeDifference( key, expected, actual ) );
	}

}
