package de.retest.recheck;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.Filters;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.PathAttribute;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.SuffixAttribute;
import de.retest.recheck.ui.descriptors.TextAttribute;
import de.retest.recheck.util.ApprovalsUtil;

class RecheckImplIT {

	static final String MESSAGE_START_PATTERN = "A detailed report will be created at '.*'";
	static final String MESSAGE_START_REPLACE = "A detailed report will be created at '*'";

	@Test
	void diff_should_be_created_accordingly() {
		execute( "no-filter", Filter.FILTER_NOTHING );
	}

	@Test
	void diff_should_be_created_if_filtered() {
		execute( "filter", Filters.parse( "matcher: retestid=same-id" ) );
	}

	void execute( final String name, final Filter filter ) {
		// No differences
		execute( name, createRootInitial(), filter );
		// Differences
		try {
			execute( name, createRootDifference(), filter );
			fail( "An assertion error should have been produced" );
		} catch ( final AssertionError e ) {
			ApprovalsUtil.verify( removeProjectSpecificPath( e.getMessage() ) );
		}
	}

	void execute( final String name, final RootElement toVerify, final Filter filter ) {
		final RecheckImpl re = new RecheckImpl( RecheckOptions.builder() //
				.setFilter( filter ) //
				.build() );

		re.startTest( name );
		re.check( toVerify, new RootElementAdapter(), "check" );
		re.capTest();

		re.cap();
	}

	String removeProjectSpecificPath( final String message ) {
		return message.replaceFirst( MESSAGE_START_PATTERN, MESSAGE_START_REPLACE );
	}

	static class RootElementAdapter implements RecheckAdapter {

		@Override
		public boolean canCheck( final Object toVerify ) {
			return toVerify instanceof RootElement;
		}

		@Override
		public Set<RootElement> convert( final Object toVerify ) {
			return Collections.singleton( (RootElement) toVerify );
		}

		@Override
		public DefaultValueFinder getDefaultValueFinder() {
			return ( identifyingAttributes, attributeKey, attributeValue ) -> {
				return "bar-3".equals( attributeKey ) && attributeValue == null; // Force default output for diffs
			};
		}
	}

	static RootElement createRootInitial() {
		final IdentifyingAttributes id = createIdentifying( "foo[1]/bar[1]", "test" );

		final MutableAttributes attributes = new MutableAttributes();
		attributes.put( "foo-1", "bar-1" );
		attributes.put( "foo-2", "bar-2" );
		attributes.put( "foo-3", "bar-3" );

		final RootElement element = new RootElement( "foo-id", id, attributes.immutable(), null, "screen", 0, "title" );
		element.addChildren( createSameFirst( element ), createDeleted( element ) );
		return element;
	}

	static RootElement createRootDifference() {
		final IdentifyingAttributes id = createIdentifying( "foo[1]/bar[1]", "test" );

		final MutableAttributes attributes = new MutableAttributes();
		attributes.put( "foo-1", "bar-3" );
		attributes.put( "foo-2", "bar-2" );
		attributes.put( "foo-3", "bar-1" );

		final RootElement element = new RootElement( "foo-id", id, attributes.immutable(), null, "screen", 0, "title" );
		element.addChildren( createSameDifference( element ), createInserted( element ) );
		return element;
	}

	static Element createSameFirst( final Element parent ) {
		return create( parent, "foo[1]/bar[1]/same[1]", "same", "" );
	}

	static Element createSameDifference( final Element parent ) {
		return create( parent, "foo[1]/bar[1]/same[1]", "same", "-change" );
	}

	static Element createDeleted( final Element parent ) {
		return create( parent, "foo[1]/bar[1]/remove[1]/delete[1]", "delete", "" );
	}

	static Element createInserted( final Element parent ) {
		return create( parent, "foo[1]/bar[1]/add[1]/insert[1]", "insert", "" );
	}

	static Element create( final Element parent, final String path, final String type, final String prefix ) {
		final IdentifyingAttributes identifyingAttributes = createIdentifying( path, type );

		final MutableAttributes attributes = new MutableAttributes();
		attributes.put( "bar-1", "bar-1" + prefix );
		attributes.put( "bar-2" + prefix, "bar-2" );
		attributes.put( "bar-3" + prefix, "bar-3" );

		return Element.create( type + "-id", parent, identifyingAttributes, attributes.immutable(), null );
	}

	private static IdentifyingAttributes createIdentifying( final String path, final String type ) {
		final Path typed = Path.fromString( path );

		return new IdentifyingAttributes( Arrays.asList( //
				new PathAttribute( typed ), //
				new SuffixAttribute( typed.getElement().getSuffix() ), //
				new StringAttribute( IdentifyingAttributes.TYPE_ATTRIBUTE_KEY, type ), //
				new TextAttribute( "text", type ), //
				new TextAttribute( "name", type ) //
		) );
	}
}
