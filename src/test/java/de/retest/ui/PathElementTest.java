package de.retest.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathElementTest {

	@Test
	public void string_representation() throws Exception {
		assertThat( new PathElement( "element", 1 ).toString() ).isEqualTo( "element[1]" );
		assertThat( new PathElement( "element", 1 ).toString() ).isEqualTo( "element[1]" );
		assertThat( new PathElement( "element" ).toString() ).isEqualTo( "element[1]" );
	}

	@Test( expected = IllegalArgumentException.class )
	public void empty_path_element_throws_exception() {
		new PathElement( "", 1 );
	}

	@Test( expected = NullPointerException.class )
	public void null_path_element_throws_exception() {
		new PathElement( null, 1 );
	}

	@Test( expected = IllegalArgumentException.class )
	public void blank_path_element_throws_exception() {
		new PathElement( " ", 1 );
	}

	@Test( expected = IllegalArgumentException.class )
	public void tab_path_element_throws_exception() {
		new PathElement( "	", 1 );
	}

	@Test
	public void input_should_be_trimmed() {
		assertThat( new PathElement( "	element ", 1 ).toString() ).isEqualTo( "element[1]" );
		assertThat( new PathElement( "element ", 1 ).toString() ).isEqualTo( "element[1]" );
		assertThat( new PathElement( "element ", 1 ).toString() ).isEqualTo( "element[1]" );
	}
}
