package de.retest.recheck.ui.descriptors.idproviders;

import static de.retest.recheck.ui.Path.fromString;
import static de.retest.recheck.ui.descriptors.IdentifyingAttributes.create;
import static de.retest.recheck.ui.descriptors.IdentifyingAttributes.createList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.SuffixAttribute;
import de.retest.recheck.ui.descriptors.idproviders.DefaultRetestIdProvider;

public class DefaultRetestIdProviderTest {

	DefaultRetestIdProvider cut;

	@Before
	public void setUp() {
		cut = new DefaultRetestIdProvider();
	}

	@Test
	public void too_long_text_should_be_cut() {
		// for what you would actually call a text
		final IdentifyingAttributes ident = createIdentAttributes(
				"This is some very long sentence, that could be in a link text, or in some paragraph, and really is to long to be used as id." );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId.length() ).isLessThan( 20 );

		// and cut should still be unique!
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	@Test
	public void too_long_words_should_be_cut() {
		// but also for single words
		final IdentifyingAttributes ident = createIdentAttributes( "supercalifragilisticexpialidocious" );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId.length() ).isLessThan( 20 );

		// and cut should still be unique!
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	private IdentifyingAttributes createIdentAttributes( final String text ) {
		final Collection<Attribute> attributes = createList( fromString( "/HTML/DIV[1]" ), "DIV" );
		attributes.add( new StringAttribute( "text", text ) );
		return new IdentifyingAttributes( attributes );
	}

	@Test
	public void should_always_be_unique() {
		final IdentifyingAttributes ident = createIdentAttributes( "a" );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	@Test
	public void works_even_only_for_path_and_type() {
		final IdentifyingAttributes ident = create( fromString( "/HTML/DIV[1]" ), "DIV" );
		final String id1 = cut.getRetestId( ident );
		final String id2 = cut.getRetestId( ident );
		assertThat( id1 ).isNotEqualTo( id2 );
	}

	@Test
	public void no_text_should_give_type() {
		final Collection<Attribute> attributes = createList( fromString( "/HTML/DIV[1]" ), "DIV" );
		attributes.add( new StringAttribute( "type", "DIV" ) );
		attributes.add( new SuffixAttribute( 3 ) );
		assertThat( cut.getRetestId( new IdentifyingAttributes( attributes ) ) ).isEqualTo( "div" );
	}

	@Test( expected = NullPointerException.class )
	public void null_should_give_exception() {
		cut.getRetestId( null );
	}

	@Test( expected = NullPointerException.class )
	public void null_path_should_give_exception() {
		cut.getRetestId( create( null, "DIV" ) );
	}

	@Test
	public void empty_text_should_yield_id_based_on_type() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "text" ) ).thenReturn( "" );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "SomeType" );
		assertThat( cut.getRetestId( identifyingAttributes ) ).isEqualTo( "sometype" );
	}

	@Test
	public void empty_text_or_type_should_result_in_non_empty_id() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "text" ) ).thenReturn( "" );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "" );
		assertThat( cut.getRetestId( identifyingAttributes ) ).isNotEmpty();
	}

	@Test
	public void invalid_text_or_type_should_result_in_non_empty_id() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "text" ) ).thenReturn( "+" );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "+" );
		final String id = cut.getRetestId( identifyingAttributes );
		assertThat( id ).isNotEmpty();
	}

	@Test
	public void already_known_ids_should_get_a_unique_suffix() {
		final String text = "foo";

		final IdentifyingAttributes identifyingAttributes0 = mock( IdentifyingAttributes.class );
		when( identifyingAttributes0.get( "text" ) ).thenReturn( text );

		final String retestId0 = cut.getRetestId( identifyingAttributes0 );
		assertThat( retestId0 ).isEqualTo( text );

		final IdentifyingAttributes identifyingAttributes1 = mock( IdentifyingAttributes.class );
		when( identifyingAttributes1.get( "text" ) ).thenReturn( text );

		final String retestId1 = cut.getRetestId( identifyingAttributes1 );
		assertThat( retestId1 ).startsWith( text );

		assertThat( retestId0 ).isNotEqualTo( retestId1 );
	}

	@Test
	public void should_produce_sensible_results() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "javax.swing.Box$Filler" );
		final String id = cut.getRetestId( identifyingAttributes );
		assertThat( id ).isEqualTo( "boxfiller" );
	}
}
