package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Rectangle;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.retest.recheck.ignore.Filter.ChangeType;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import io.github.netmikey.logunit.api.LogCapturer;

class JSFilterImplTest {

	@RegisterExtension
	LogCapturer warningAndErrorLogs = LogCapturer.create() //
			.captureForType( JSFilterImpl.class );

	Path ctorArg = Paths.get( "nonExistentFilterFile" );

	@Test
	void no_matches_function_should_not_cause_exception() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "" );
			}
		};
		cut.matches( mock( Element.class ) );
	}

	@Test
	void invalid_matches_function_should_not_cause_exception() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "asdasd.asd.asd();" );
			}
		};
		cut.matches( mock( Element.class ) );
	}

	@Test
	void nonexistent_file_should_not_cause_exception() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
		};
		cut.matches( mock( Element.class ) );
	}

	@Test
	void matches_should_be_called() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "function matches(element) { return true; }" );
			}
		};
		assertThat( cut.matches( mock( Element.class ) ) ).isTrue();
	}

	@Test
	void matches_with_changetype_should_be_called() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "function matches(element, changetype) { return changetype == 'inserted'; }" );
			}
		};
		assertThat( cut.matches( mock( Element.class ), ChangeType.INSERTED ) ).isTrue();
		assertThat( cut.matches( mock( Element.class ), ChangeType.DELETED ) ).isFalse();
		assertThat( cut.matches( mock( Element.class ), ChangeType.CHANGED ) ).isFalse();
	}

	@Test
	void matches_should_be_called_with_element_param() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function matches(element) { " //
								+ "if (element != null) {" //
								+ "  return true;" //
								+ "} " //
								+ "  return false;" //
								+ "}" );
			}
		};
		assertThat( cut.matches( mock( Element.class ) ) ).isTrue();
	}

	@Test
	void matches_example_implementation() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function matches(element, diff) { \n" //
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
		final Element element = mock( Element.class );
		assertThat( cut.matches( element, new AttributeDifference( "outline", new Rectangle( 580, 610, 200, 20 ),
				new Rectangle( 578, 605, 200, 20 ) ) ) ).isTrue();
		assertThat( cut.matches( element, new AttributeDifference( "outline", new Rectangle( 580, 610, 200, 20 ),
				new Rectangle( 500, 605, 200, 20 ) ) ) ).isFalse();
	}

	@Test
	void matches_return_null_should_be_false() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function matches(element, diff) { " //
								+ "  return;" //
								+ "}" );
			}
		};
		final Element element = mock( Element.class );
		assertThat( cut.matches( element, new AttributeDifference( "outline", "580", "578" ) ) ).isFalse();
	}

	@Test
	void matches_return_non_boolean_should_not_throw_exc() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function matches(element, diff) { " //
								+ "  return \"this is not a bool\";" //
								+ "}" );
			}
		};
		final Element element = mock( Element.class );
		assertThat( cut.matches( element, new AttributeDifference( "outline", "580", "578" ) ) ).isFalse();
	}

	@Test
	void matches_filter_URL_example_implementation() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function matches(element, diff) {" //
								+ "  var re = /http[s]?:\\/\\/[\\w.:\\d\\-]*/;" //
								+ "  var expected = new String(diff.expected);" //
								+ "  var actual = new String(diff.actual);" //
								+ "  cleanExpected = expected.replace(re, '');" //
								+ "  cleanActual = actual.replace(re, '');" //
								+ "  return cleanExpected === cleanActual;" //
								+ "}" );
			}
		};
		final Element element = mock( Element.class );
		assertThat( cut.matches( element, new AttributeDifference( "background-image",
				"url(\"https://www2.test.k8s.bigcct.be/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")",
				"url(\"http://icullen-website-public-spring4-8:8080/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")" ) ) )
						.isTrue();
		assertThat( cut.matches( element, new AttributeDifference( "background-image",
				"url(\"https://www2.test.k8s.bigcct.be/.imaging/default/dam/clients/BT_logo.svg.png/jcr:content.png\")",
				"url(\"http://icullen-website-public-spring4-8:8080/some-other-URL.png\")" ) ) ).isFalse();
	}

	@Test
	void calling_erroneous_method_twice_with_different_args_should_actually_call_method() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( //
						"function matches(element) { " //
								+ "if (element != null) {" //
								+ "  return true;" //
								+ "}" //
								+ "throw 42;" //
								+ "}" );
			}
		};
		cut.matches( null );
		assertThat( cut.matches( mock( Element.class ) ) ).isTrue();
	}

	@Test
	void no_method_warning_should_only_be_printed_once_for_element() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "" );
			}
		};
		cut.matches( mock( Element.class ) );
		cut.matches( mock( Element.class ) );
		assertThat( warningAndErrorLogs.size() ).isEqualTo( 1 );
	}

	@Test
	void no_method_warning_should_only_be_printed_once_for_element_and_diff() {
		final JSFilterImpl cut = new JSFilterImpl( ctorArg ) {
			@Override
			Reader readScriptFile( final Path path ) {
				return new StringReader( "" );
			}
		};
		cut.matches( mock( Element.class ), mock( AttributeDifference.class ) );
		cut.matches( mock( Element.class ), mock( AttributeDifference.class ) );
		assertThat( warningAndErrorLogs.size() ).isEqualTo( 1 );
	}
}
