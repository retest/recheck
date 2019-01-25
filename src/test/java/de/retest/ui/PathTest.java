package de.retest.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathTest {

	private static final PathElement ELEMENT_0 = new PathElement( "elem", 1 );
	private static final PathElement ELEMENT_1 = new PathElement( "leleme", 1 );
	private static final PathElement ELEMENT_2 = new PathElement( "bubi" );
	private static final PathElement ELEMENT_3 = new PathElement( "muma", 3 );

	@Test
	public void string_representation_of_simple_path() throws Exception {
		assertThat( Path.path( ELEMENT_0 ).toString() ).isEqualTo( "elem[1]" );
		assertThat( Path.fromString( "elem[1]" ) ).isEqualTo( Path.path( ELEMENT_0 ) );
	}

	@Test
	public void string_representation_of_nested_path_with_empty_path() throws Exception {
		assertThat( Path.path( ELEMENT_0 ).toString() ).isEqualTo( "elem[1]" );
		assertThat( Path.fromString( "elem[1]" ) ).isEqualTo( Path.path( ELEMENT_0 ) );
	}

	@Test
	public void no_slash_before_root() {
		assertThat( Path.fromString( "/elem[1]" ) ).isEqualTo( Path.path( ELEMENT_0 ) );
		assertThat( Path.fromString( "//elem[1]" ) ).isEqualTo( Path.path( ELEMENT_0 ) );
		assertThat( Path.fromString( "///elem[1]" ) ).isEqualTo( Path.path( ELEMENT_0 ) );
	}

	@Test
	public void string_representation_of_nested_parent_path() throws Exception {
		assertThat( Path.path( Path.path( ELEMENT_0 ), ELEMENT_1 ).toString() ).isEqualTo( "elem[1]/leleme[1]" );
	}

	@Test
	public void string_representation_of_multilevel_nested_parent_path() throws Exception {
		final Path path0 = Path.path( ELEMENT_0 );
		final Path path1 = Path.path( path0, ELEMENT_1 );
		final Path path2 = Path.path( path1, ELEMENT_2 );
		final Path path3 = Path.path( path2, ELEMENT_3 );
		assertThat( path3.toString() ).isEqualTo( "elem[1]/leleme[1]/bubi[1]/muma[3]" );
		assertThat( Path.fromString( "elem[1]/leleme[1]/bubi[1]/muma[3]" ) ).isEqualTo( path3 );
	}

	@Test
	public void test_parentPath() {
		final Path path1 = Path.path( Path.path( ELEMENT_0 ), ELEMENT_1 );
		assertThat( Path.path( ELEMENT_0 ).isParent( path1 ) ).isTrue();
		assertThat( path1.isParent( path1 ) ).isTrue();
		assertThat( Path.path( ELEMENT_1 ).isParent( path1 ) ).isFalse();
	}

	@Test
	public void trailing_slash_should_not_cause_exception() {
		Path.fromString( "/html/body/div/" );
		Path.fromString( "/HTML/BODY/DIV/" );
	}
}
