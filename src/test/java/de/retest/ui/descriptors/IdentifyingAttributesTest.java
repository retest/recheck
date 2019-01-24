package de.retest.ui.descriptors;

import static de.retest.ui.Path.fromString;
import static de.retest.ui.descriptors.IdentifyingAttributes.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.retest.ui.Path;
import de.retest.ui.diff.AttributeDifference;

public class IdentifyingAttributesTest {

	public static final double ONE_SIMILARITY = 1.0 / IdentifyingAttributes.PERFECT_SIMILARITY;

	private final Path path = Path.fromString( "Window[1]/path[1]/component[1]" );

	@Test
	public void same_attributes_should_match_100_percent() {
		final IdentifyingAttributes expected =
				IdentifyingAttributes.create( Path.fromString( "Window[1]/path[1]/component[1]" ), component.class );
		final IdentifyingAttributes actual =
				IdentifyingAttributes.create( Path.fromString( "Window[1]/path[1]/component[1]" ), component.class );

		assertThat( expected.match( actual ) ).isCloseTo( 1.0, within( 0.001 ) );
		assertThat( expected.hashCode() ).isEqualTo( actual.hashCode() );
		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	public void more_actual_attributes_should_result_in_difference() throws Exception {
		final Collection<Attribute> attributes = IdentifyingAttributes
				.createList( Path.fromString( "Window[1]/path[1]/component[1]" ), component.class.getName() );
		final IdentifyingAttributes expected = new IdentifyingAttributes( attributes );
		attributes.add( new StringAttribute( "myKey", "some value" ) );
		final IdentifyingAttributes actual = new IdentifyingAttributes( attributes );

		assertThat( expected.match( actual ) ).isLessThan( 1.0 );
		assertThat( expected.hashCode() ).isNotEqualTo( actual.hashCode() );
		assertThat( expected ).isNotEqualTo( actual );
	}

	@Test
	public void more_expected_attributes_should_result_in_difference() throws Exception {
		final Collection<Attribute> attributes = IdentifyingAttributes
				.createList( Path.fromString( "Window[1]/path[1]/component[1]" ), component.class.getName() );
		final IdentifyingAttributes actual = new IdentifyingAttributes( attributes );
		attributes.add( new StringAttribute( "myKey", "some value" ) );
		final IdentifyingAttributes expected = new IdentifyingAttributes( attributes );

		assertThat( expected.match( actual ) ).isLessThan( 1.0 );
		assertThat( expected.hashCode() ).isNotEqualTo( actual.hashCode() );
		assertThat( expected ).isNotEqualTo( actual );
	}

	@Test
	public void no_out_of_two_attributes_should_match_0_percent() {
		final IdentifyingAttributes expected =
				IdentifyingAttributes.create( Path.fromString( "a[1]/component[1]" ), component.class );
		final IdentifyingAttributes actual =
				IdentifyingAttributes.create( Path.fromString( "b[2]/component[2]" ), otherComponent.class );

		assertThat( expected ).isNotEqualTo( actual );
		assertThat( expected.hashCode() ).isNotEqualTo( actual.hashCode() );
		assertThat( expected.match( actual ) ).isCloseTo( 0.0, within( 0.01 ) );
	}

	@Test
	public void only_same_parent_path_results_in_1_similarity() {
		// TODO Maybe the counter should be indifferent to type?
		final IdentifyingAttributes expected =
				IdentifyingAttributes.create( Path.fromString( "Window[1]/path[1]/component[1]" ), component.class );
		final IdentifyingAttributes actual = IdentifyingAttributes
				.create( Path.fromString( "Window[1]/path[1]/component[2]" ), otherComponent.class );

		assertThat( expected.match( actual ) ).isCloseTo( ONE_SIMILARITY, within( 0.01 ) );
	}

	@Test
	public void several_differences_should_decrease_similarity() {
		final IdentifyingAttributes cell_0_4_orig = IdentifyingAttributes.create( Path.fromString(
				"Window[1]/JRootPane[1]/JLayeredPane[1]/StatefulTableDemo[1]/JTabbedPane[1]/Tab[1]/JPanel[1]/JTable[1]/row[1]/column[5]" ),
				cell.class );
		final IdentifyingAttributes cell_0_4_new = IdentifyingAttributes.create( Path.fromString(
				"Window[1]/JRootPane[1]/JLayeredPane[1]/StatefulTableDemo[1]/JTabbedPane[1]/Tab[2]/JPanel[1]/JTable[1]/row[1]/column[5]" ),
				cell.class );
		final IdentifyingAttributes cell_2_4_new = IdentifyingAttributes.create( Path.fromString(
				"Window[1]/JRootPane[1]/JLayeredPane[1]/StatefulTableDemo[1]/JTabbedPane[1]/Tab[2]/JPanel[1]/JTable[1]/row[3]/column[5]" ),
				cell.class );

		assertThat( cell_0_4_orig.match( cell_0_4_new ) ).isGreaterThan( cell_0_4_orig.match( cell_2_4_new ) );
	}

	@Test
	public void apply_same_value_should_work() throws Exception {
		final String pathValue = "Window[1]/JRootPane[1]/JLayeredPane[1]/JTable[1]/row[7]/column[1]";
		final IdentifyingAttributes identifyingAttributes =
				IdentifyingAttributes.create( fromString( pathValue ), component.class );
		identifyingAttributes
				.applyChanges( createAttributeChanges( fromString( pathValue ), "path", pathValue, pathValue ) );
	}

	@Test
	public void apply_path_change() {
		final IdentifyingAttributes identifyingAttributes = IdentifyingAttributes.create( path, component.class );

		final AttributeDifference pathDifference =
				new AttributeDifference( "path", Path.fromString( "Window[1]/path[1]/component[1]" ),
						Path.fromString( "Window[1]/otherpath[1]/component[2]" ) );
		final AttributeDifference suffixDifference = new AttributeDifference( "suffix", "0", "1" );
		final IdentifyingAttributes changed = identifyingAttributes
				.applyChanges( new HashSet<>( Arrays.asList( pathDifference, suffixDifference ) ) );

		assertThat( changed ).isNotEqualTo( identifyingAttributes );
		assertThat( changed.getPath() ).isEqualTo( "Window[1]/otherpath[1]/component[2]" );
		assertThat( changed.getSuffix() ).isEqualTo( "1" );
		assertThat( changed.getParentPath() ).isEqualTo( "Window[1]/otherpath[1]" );
	}

	@Test( expected = NullPointerException.class )
	public void path_should_not_be_set_to_null() {
		final IdentifyingAttributes identifyingAttributes = IdentifyingAttributes.create( path, component.class );

		identifyingAttributes.applyChanges(
				createAttributeChanges( path, "path", Path.fromString( "Window[1]/path[1]/component[1]" ), null ) );
	}

	@Test
	public void apply_type_change() {
		final IdentifyingAttributes identifyingAttributes = IdentifyingAttributes.create( path, component.class );

		final IdentifyingAttributes changed = identifyingAttributes.applyChanges(
				createAttributeChanges( path, "type", component.class.getName(), otherComponent.class.getName() ) );

		assertThat( changed ).isNotEqualTo( identifyingAttributes );
		assertThat( changed.getType() ).isEqualTo( otherComponent.class.getName() );
	}

	@Test
	public void apply_different_path_value_should_work() {
		final String originalPath = "Window[1]/path[1]/component[1]";
		final String changedPath = "Window[1]/new_path[1]/new_component[1]";
		final IdentifyingAttributes original =
				IdentifyingAttributes.create( Path.fromString( originalPath ), component.class );

		final IdentifyingAttributes changed = original
				.applyChanges( createAttributeChanges( path, PathAttribute.PATH_KEY, originalPath, changedPath ) );

		assertThat( (Path) changed.get( PathAttribute.PATH_KEY ) ).isEqualTo( Path.fromString( changedPath ) );
	}

	@Test
	public void different_outline_should_result_in_change() {
		final List<Attribute> attributes = new ArrayList<>();
		attributes.add( new PathAttribute( Path.fromString( "HTML[1]/DIV[1]" ) ) );
		attributes.add( new SuffixAttribute( 1 ) );
		attributes.add( new StringAttribute( "type", "DIV" ) );

		final List<Attribute> originalAttributes = new ArrayList<>( attributes );
		originalAttributes.add( OutlineAttribute.create( new Rectangle( 0, 0, 800, 1200 ) ) );
		final IdentifyingAttributes original = new IdentifyingAttributes( originalAttributes );

		final List<Attribute> differentOutlineAttributes = new ArrayList<>( attributes );
		originalAttributes.add( OutlineAttribute.create( new Rectangle( 0, 0, 400, 600 ) ) );
		final IdentifyingAttributes differentOutline = new IdentifyingAttributes( differentOutlineAttributes );

		assertThat( original.match( differentOutline ) ).isLessThan( 1.0 );
	}

	@Test( expected = IllegalArgumentException.class )
	public void blank_type_should_give_exception() {
		create( fromString( "/HTML/DIV" ), " " );
	}

	@Test( expected = IllegalArgumentException.class )
	public void tab_type_should_give_exception() {
		create( fromString( "/HTML/DIV" ), "	" );
	}

	@Test( expected = IllegalArgumentException.class )
	public void empty_type_should_give_exception() {
		create( fromString( "/HTML/DIV" ), "" );
	}

	@Test( expected = NullPointerException.class )
	public void null_String_type_should_give_exception() {
		create( fromString( "/HTML/DIV" ), (String) null );
	}

	@Test( expected = NullPointerException.class )
	public void null_Class_type_should_give_exception() {
		create( fromString( "/HTML/DIV" ), (Class<?>) null );
	}

	private Set<AttributeDifference> createAttributeChanges( final Path path, final String key,
			final Serializable expected, final Serializable actual ) {
		return Collections.singleton( new AttributeDifference( key, expected, actual ) );
	}

	private static class component {}

	private static class otherComponent {}

	private static class cell {}

}
