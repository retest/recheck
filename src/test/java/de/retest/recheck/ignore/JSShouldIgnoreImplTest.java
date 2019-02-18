package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class JSShouldIgnoreImplTest {

	@Test
	void no_shouldIgnoreElement_function_should_not_cause_exception() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl() {
			@Override
			Reader readScriptFile() {
				return new StringReader( "" );
			}
		};
		cut.shouldIgnoreElement( Mockito.mock( Element.class ) );
	}

	@Test
	void shouldIgnoreElement_should_be_called() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl() {
			@Override
			Reader readScriptFile() {
				return new StringReader( "function shouldIgnoreElement(element) { return true; }" );
			}
		};
		assertThat( cut.shouldIgnoreElement( Mockito.mock( Element.class ) ) ).isTrue();
	}

	@Test
	void shouldIgnoreElement_should_be_called_with_element_param() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl() {
			@Override
			Reader readScriptFile() {
				return new StringReader( //
						"function shouldIgnoreElement(element) { " //
								+ "if (element != null) {" //
								+ "  return true;" //
								+ "} " //
								+ "return false;" //
								+ "}" );
			}
		};
		assertThat( cut.shouldIgnoreElement( Mockito.mock( Element.class ) ) ).isTrue();
	}

	@Test
	void shouldIgnoreAttributeDifference_example_implementation() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl() {
			@Override
			Reader readScriptFile() {
				return new StringReader( //
						"function shouldIgnoreAttributeDifference(element, diff) { " //
								+ "  if (diff.key == 'outline') {" //
								+ "    return Math.abs(diff.expected - diff.actual) < 5;" //
								+ "  } " //
								+ "  return false;" //
								+ "}" );
			}
		};
		final Element element = Mockito.mock( Element.class );
		assertThat( cut.shouldIgnoreAttributeDifference( element, new AttributeDifference( "outline", "580", "578" ) ) )
				.isTrue();
		assertThat( cut.shouldIgnoreAttributeDifference( element, new AttributeDifference( "outline", "580", "500" ) ) )
				.isFalse();
	}

	@Test
	void shouldIgnoreAttributeDifference_ignore_URL_example_implementation() {
		final JSShouldIgnoreImpl cut = new JSShouldIgnoreImpl() {
			@Override
			Reader readScriptFile() {
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
		assertThat( cut.shouldIgnoreAttributeDifference( element, new AttributeDifference( "background-image",
				"url(\"https://www2.test.k8s.bigcct.be/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")",
				"url(\"http://icullen-website-public-spring4-8:8080/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")" ) ) )
						.isTrue();
		assertThat( cut.shouldIgnoreAttributeDifference( element, new AttributeDifference( "background-image",
				"url(\"https://www2.test.k8s.bigcct.be/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")",
				"url(\"http://icullen-website-public-spring4-8:8080/some-other-URL.png\")" ) ) ).isFalse();
	}
}
