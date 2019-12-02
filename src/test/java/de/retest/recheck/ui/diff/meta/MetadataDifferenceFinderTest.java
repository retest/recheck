package de.retest.recheck.ui.diff.meta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.SutState;

class MetadataDifferenceFinderTest {

	MetadataDifferenceFinder cut;

	@BeforeEach
	void setUp() {
		cut = new MetadataDifferenceFinder();
	}

	@Test
	void findDifferences_with_empty_states_should_not_create_differences() throws Exception {
		assertThat( cut.findDifferences( state( Collections.emptyMap() ), state( Collections.emptyMap() ) ) ).isEmpty();
	}

	@Test
	void findDifferences_with_equal_states_should_not_create_differences() throws Exception {
		final SutState state = state( hash( "k1", "a" ) );
		assertThat( cut.findDifferences( state, state ) ).isEmpty();
	}

	@Test
	void findDifferences_with_actual_empty_should_create_deleted_differences() throws Exception {
		assertThat( cut.findDifferences( state( hash( "k1", "a" ) ), state( Collections.emptyMap() ) ) ) //
				.hasSize( 1 ) //
				.allSatisfy( difference -> {
					assertThat( difference.getKey() ).isNotNull();
					assertThat( difference.getExpected() ).isEqualTo( "a" );
					assertThat( difference.getActual() ).isNull();
				} );
	}

	@Test
	void findDifferences_with_expected_empty_should_create_inserted_differences() throws Exception {
		assertThat( cut.findDifferences( state( Collections.emptyMap() ), state( hash( "k1", "a" ) ) ) ) //
				.hasSize( 1 ) //
				.allSatisfy( difference -> {
					assertThat( difference.getKey() ).isNotNull();
					assertThat( difference.getExpected() ).isNull();
					assertThat( difference.getActual() ).isEqualTo( "a" );
				} );
	}

	@Test
	void findDifferences_with_actual_difference_should_find_differences() throws Exception {
		assertThat( cut.findDifferences( state( hash( "k1", "a" ) ), state( hash( "k1", "b" ) ) ) ) //
				.hasSize( 1 ) //
				.allSatisfy( difference -> {
					assertThat( difference.getKey() ).isNotNull();
					assertThat( difference.getExpected() ).isEqualTo( "a" );
					assertThat( difference.getActual() ).isEqualTo( "b" );
				} );
	}

	@Test
	void findDifferences_should_not_care_about_internal_map_ordering() throws Exception {
		final Map<String, String> expected = new TreeMap<>( Comparator.naturalOrder() );
		expected.put( "k1", "a" );
		expected.put( "k2", "a" );
		expected.put( "k3", "a" );

		final Map<String, String> actual = new TreeMap<>( Comparator.reverseOrder() );
		actual.put( "k1", "b" );
		actual.put( "k2", "b" );
		actual.put( "k3", "b" );

		assertThat( cut.findDifferences( state( expected ), state( actual ) ) ) //
				.hasSize( 3 ) //
				.allSatisfy( difference -> {
					assertThat( difference.getKey() ).isNotNull();
					assertThat( difference.getExpected() ).isEqualTo( "a" );
					assertThat( difference.getActual() ).isEqualTo( "b" );
				} );
	}

	private SutState state( final Map<String, String> metadata ) {
		return new SutState( Collections.emptySet(), () -> metadata );
	}

	private Map<String, String> hash( final String k1, final String v1 ) {
		final Map<String, String> map = new HashMap<>();
		map.put( k1, v1 );
		return map;
	}
}
