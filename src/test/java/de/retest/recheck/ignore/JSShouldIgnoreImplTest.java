package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Rectangle;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class JSShouldIgnoreImplTest {

	@Test
	void no_shouldIgnoreElement_function_should_not_cause_exception() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "" );
			}
		};
		cut.filterElement( Mockito.mock( Element.class ) );
	}

	@Test
	void invalid_shouldIgnoreElement_function_should_not_cause_exception() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "asdasd.asd.asd();" );
			}
		};
		cut.filterElement( Mockito.mock( Element.class ) );
	}

	@Test
	void nonexistent_file_should_not_cause_exception() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {};
		cut.filterElement( Mockito.mock( Element.class ) );
	}

	@Test
	void shouldIgnoreElement_should_be_called() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "function shouldIgnoreElement(element) { return true; }" );
			}
		};
		assertThat( cut.filterElement( Mockito.mock( Element.class ) ) ).isTrue();
	}

	@Test
	void shouldIgnoreElement_should_be_called_with_element_param() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function shouldIgnoreElement(element) { " //
								+ "if (element != null) {" //
								+ "  return true;" //
								+ "} " //
								+ "return false;" //
								+ "}" );
			}
		};
		assertThat( cut.filterElement( Mockito.mock( Element.class ) ) ).isTrue();
	}

	@Test
	void shouldIgnoreAttributeDifference_example_implementation() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function shouldIgnoreAttributeDifference(element, diff) { \n" //
								+ "  if (diff.key == 'outline') {\n" //
								+ "    return (Math.abs(diff.expected.x - diff.actual.x) <= 5) && \n" //
								+ "			  (Math.abs(diff.expected.y - diff.actual.y) <= 5) && \n" //
								+ "			  (Math.abs(diff.expected.width - diff.actual.width) <= 5) && \n" //
								+ "			  (Math.abs(diff.expected.height - diff.actual.height) <= 5);\n" //
								+ "  } \n" //
								+ "  return false;\n" //
								+ "}\n" );
			}
		};
		final Element element = Mockito.mock( Element.class );
		assertThat( cut.filterAttributeDifference( element, new AttributeDifference( "outline",
				new Rectangle( 580, 610, 200, 20 ), new Rectangle( 578, 605, 200, 20 ) ) ) ).isTrue();
		assertThat( cut.filterAttributeDifference( element, new AttributeDifference( "outline",
				new Rectangle( 580, 610, 200, 20 ), new Rectangle( 500, 605, 200, 20 ) ) ) ).isFalse();
	}

	@Test
	void shouldIgnoreAttributeDifference_return_null_should_be_false() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function shouldIgnoreAttributeDifference(element, diff) { " //
								+ "  return;" //
								+ "}" );
			}
		};
		final Element element = Mockito.mock( Element.class );
		assertThat( cut.filterAttributeDifference( element, new AttributeDifference( "outline", "580", "578" ) ) )
				.isFalse();
	}

	@Test
	void shouldIgnoreAttributeDifference_return_non_boolean_should_throw_exc() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function shouldIgnoreAttributeDifference(element, diff) { " //
								+ "  return \"this is not a bool\";" //
								+ "}" );
			}
		};
		final Element element = Mockito.mock( Element.class );
		assertThrows( ClassCastException.class, () -> cut.filterAttributeDifference( element,
				new AttributeDifference( "outline", "580", "578" ) ) );
	}

	@Test
	void shouldIgnoreAttributeDifference_ignore_URL_example_implementation() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl( null ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function shouldIgnoreAttributeDifference(element, diff) { " //
								+ "  re = /http[s]?:\\/\\/[\\w.:\\d\\-]*/;" //
								+ "  cleanExpected = diff.expected.replace(re, '');" //
								+ "  cleanActual = diff.actual.replace(re, '');" //
								+ "  return cleanExpected === cleanActual;" //
								+ "}" );
			}
		};
		final Element element = Mockito.mock( Element.class );
		assertThat( cut.filterAttributeDifference( element, new AttributeDifference( "background-image",
				"url(\"https://www2.test.k8s.bigcct.be/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")",
				"url(\"http://icullen-website-public-spring4-8:8080/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")" ) ) )
						.isTrue();
		assertThat( cut.filterAttributeDifference( element, new AttributeDifference( "background-image",
				"url(\"https://www2.test.k8s.bigcct.be/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")",
				"url(\"http://icullen-website-public-spring4-8:8080/some-other-URL.png\")" ) ) ).isFalse();
	}
}
