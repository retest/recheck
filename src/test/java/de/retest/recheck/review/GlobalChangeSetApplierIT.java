package de.retest.recheck.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Rectangle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.Recheck;
import de.retest.recheck.RecheckAdapter;
import de.retest.recheck.RecheckImpl;
import de.retest.recheck.RecheckOptions;
import de.retest.recheck.SuiteAggregator;
import de.retest.recheck.persistence.SeparatePathsProjectLayout;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.PathElement;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.OutlineAttribute;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.ElementIdentificationWarning;
import lombok.extern.slf4j.Slf4j;

class GlobalChangeSetApplierIT {

	GlobalChangeSetApplier cut;

	@BeforeEach
	void setUp( @TempDir final Path root ) throws Exception {
		SuiteAggregator.reset();

		Files.createDirectory( root.resolve( ".retest" ) );

		final RecheckAdapter adapter = new RootElementRecheckAdapter();

		final Recheck re = new RecheckImpl( RecheckOptions.builder() //
				.suiteName( "TemplateTest" ) //
				.projectLayout( new SeparatePathsProjectLayout( root.resolve( "states" ), root.resolve( "reports" ) ) ) //
				.build() );

		// Create first step to create the Golden Masters 

		re.startTest( "large" );
		re.check( expected( 1200, 800 ), adapter, "page" );
		capTest( re );

		re.startTest( "small" );
		re.check( expected( 800, 400 ), adapter, "page" );
		capTest( re );

		// Create second step with slight pixel differences to force the identifying attributes to contain differences

		re.startTest( "large" );
		re.check( actual( 1190, 795 ), adapter, "page" );
		capTest( re );

		re.startTest( "small" );
		re.check( actual( 750, 390 ), adapter, "page" );
		capTest( re );

		re.cap();

		final TestReport report = SuiteAggregator.getInstance().getAggregatedTestReport();
		cut = GlobalChangeSetApplier.create( report );
	}

	@Test
	void lookups_should_ignore_outline_changes() {
		// Differences:
		// html[1]/body[1]/div[1]
		// - outline: expected="[0,0,300,200]", actual="[0,0,297,198]" (large)
		// - outline: expected="[0,0,200,100]", actual="[0,0,187,97]" (small)
		// - align-self: expected="", actual="flex-start" (large, small)
		assertThat( cut.getAttributeDiffsLookupMap().asMap() ).hasSize( 3 );

		// Inserted:
		// html[1] (large, small)
		assertThat( cut.getInsertedDiffsLookupMap().asMap() ).hasSize( 1 );

		// Deleted:
		// html[1]/body[1]/span[1] (large, small)
		assertThat( cut.getDeletedDiffsLookupMap().asMap() ).hasSize( 1 );
	}

	@Test
	void lookups_should_contain_all_warnings() {
		// There are two differences for align-self (large, small)
		assertThat( cut.getWarningsLookup().values() ).flatExtracting( Function.identity() ).hasSize( 2 );
	}

	private void capTest( final Recheck re ) {
		try {
			re.capTest();
		} catch ( final AssertionError error ) {
			// Ignore
		}
	}

	private RootElement expected( final int width, final int height ) {
		final RootElement html = html();
		final Element body = body( html );
		final Element div = div( body, 1, width, height, attributes( "align-self", "" ) );
		final Element span = span( body, 1, width, height, new Attributes() );

		html.addChildren( body );
		body.addChildren( div, span );

		return html;
	}

	private RootElement actual( final int width, final int height ) {
		final RootElement html = html();
		final Element body = body( html );
		final Element div = div( body, 1, width, height, attributes( "align-self", "flex-start" ) );

		html.addChildren( body );
		body.addChildren( div );

		return html;
	}

	private RootElement html() {
		return new RootElement( "html", id( path( "html", 1 ) ), new Attributes(), null, "screen", 0, "title" );
	}

	private Element body( final Element parent ) {
		return Element.create( "div", parent, id( path( parent, "body", 1 ) ), new Attributes() );
	}

	private Element div( final Element parent, final int suffix, final int width, final int height,
			final Attributes attributes ) {
		return Element.create( "div", parent, id( path( parent, "div", suffix ), //
				OutlineAttribute.createAbsolute( new Rectangle( width / 2, height / 2, width / 4, height / 4 ) ), //
				OutlineAttribute.create( new Rectangle( 0, 0, width / 4, height / 4 ) ), //
				new StringAttribute( "class", "md-sidebar md-sidebar--primary" ) // 
		), attributes );
	}

	private Element span( final Element parent, final int suffix, final int width, final int height,
			final Attributes attributes ) {
		return Element.create( "span", parent, id( path( parent, "span", suffix ), //
				OutlineAttribute.createAbsolute( new Rectangle( 0, height / 2, width / 2, height / 4 ) ), //
				OutlineAttribute.create( new Rectangle( 0, 0, width / 4, height / 4 ) ) //
		), attributes );
	}

	private IdentifyingAttributes id( final de.retest.recheck.ui.Path path, final Attribute... attributes ) {
		return new IdentifyingAttributes( Stream.concat( // 
				IdentifyingAttributes.createList( path, path.getElement().getElementName() ).stream(), //
				Stream.of( attributes ) //
		).collect( Collectors.toList() ) );
	}

	private de.retest.recheck.ui.Path path( final String type, final int suffix ) {
		return de.retest.recheck.ui.Path.path( new PathElement( type, suffix ) );
	}

	private de.retest.recheck.ui.Path path( final Element parent, final String type, final int suffix ) {
		return de.retest.recheck.ui.Path.path( parent.getIdentifyingAttributes().getPathTyped(),
				new PathElement( type, suffix ) );
	}

	private Attributes attributes( final String... entries ) {
		final MutableAttributes attributes = new MutableAttributes();
		for ( int i = 0; i < entries.length; i += 2 ) {
			attributes.put( entries[i], entries[i + 1] );
		}
		return attributes.immutable();
	}

	@Slf4j
	private static class RootElementRecheckAdapter implements RecheckAdapter {

		@Override
		public boolean canCheck( final Object toCheck ) {
			return toCheck instanceof RootElement;
		}

		@Override
		public Set<RootElement> convert( final Object toCheck ) {
			return Collections.singleton( (RootElement) toCheck );
		}

		@Override
		public DefaultValueFinder getDefaultValueFinder() {
			return ( identifyingAttributes, attributeKey, attributeValue ) -> false;
		}

		@Override
		public void notifyAboutDifferences( final ActionReplayResult actionReplayResult ) {
			final ElementIdentificationWarning warning = new ElementIdentificationWarning( "testFileName",
					new Random().nextInt(), "findByMethodName", "qualifiedTestName" );

			actionReplayResult.getAllElementDifferences().stream() //
					.map( ElementDifference::getAttributeDifferences ) //
					.flatMap( Collection::stream ) //
					.filter( difference -> "align-self".equals( difference.getKey() ) ) //
					.forEach( difference -> {
						log.debug( "Adding warning to difference '{}' for '{}'.", difference, actionReplayResult );
						difference.addElementIdentificationWarning( warning );
					} );
		}
	}
}
