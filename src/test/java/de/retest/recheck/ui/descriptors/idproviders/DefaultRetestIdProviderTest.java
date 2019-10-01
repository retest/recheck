package de.retest.recheck.ui.descriptors.idproviders;

import static de.retest.recheck.ui.Path.fromString;
import static de.retest.recheck.ui.descriptors.IdentifyingAttributes.create;
import static de.retest.recheck.ui.descriptors.IdentifyingAttributes.createList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.SuffixAttribute;

class DefaultRetestIdProviderTest {

	DefaultRetestIdProvider cut;

	@BeforeEach
	void setUp() {
		cut = new DefaultRetestIdProvider();
	}

	@Test
	void too_long_text_should_be_cut() {
		// for what you would actually call a text
		final IdentifyingAttributes ident = createIdentAttributes(
				"This is some very long sentence, that could be in a link text, or in some paragraph, and really is to long to be used as id." );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId.length() ).isLessThan( 20 );

		// and cut should still be unique!
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	@Test
	void too_long_words_should_be_cut() {
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
	void should_always_be_unique() {
		final IdentifyingAttributes ident = createIdentAttributes( "a" );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	@Test
	void works_even_only_for_path_and_type() {
		final IdentifyingAttributes ident = create( fromString( "/HTML/DIV[1]" ), "DIV" );
		final String id1 = cut.getRetestId( ident );
		final String id2 = cut.getRetestId( ident );
		assertThat( id1 ).isNotEqualTo( id2 );
	}

	@Test
	void no_text_should_give_type() {
		final Collection<Attribute> attributes = createList( fromString( "/HTML/DIV[1]" ), "DIV" );
		attributes.add( new StringAttribute( "type", "DIV" ) );
		attributes.add( new SuffixAttribute( 3 ) );
		assertThat( cut.getRetestId( new IdentifyingAttributes( attributes ) ) ).isEqualTo( "div" );
	}

	@Test
	void null_should_give_exception() {
		assertThatThrownBy( () -> cut.getRetestId( null ) ).isInstanceOf( NullPointerException.class );
	}

	@Test
	void null_path_should_give_exception() {
		assertThatThrownBy( () -> cut.getRetestId( create( null, "DIV" ) ) ).isInstanceOf( NullPointerException.class );
	}

	@Test
	void empty_text_should_yield_id_based_on_type() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "text" ) ).thenReturn( "" );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "SomeType" );
		assertThat( cut.getRetestId( identifyingAttributes ) ).isEqualTo( "sometype" );
	}

	@Test
	void empty_text_or_type_should_result_in_non_empty_id() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "text" ) ).thenReturn( "" );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "" );
		assertThat( cut.getRetestId( identifyingAttributes ) ).isNotEmpty();
	}

	@Test
	void invalid_text_or_type_should_result_in_non_empty_id() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "text" ) ).thenReturn( "+" );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "+" );
		final String id = cut.getRetestId( identifyingAttributes );
		assertThat( id ).isNotEmpty();
	}

	@Test
	void already_known_ids_should_get_a_unique_suffix() {
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
	void should_produce_sensible_results() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( "type" ) ).thenReturn( "javax.swing.Box$Filler" );
		final String id = cut.getRetestId( identifyingAttributes );
		assertThat( id ).isEqualTo( "boxfiller" );
	}

	@Test
	void counter_should_not_be_suffix() {
		final ElementCountingRetestIdProvider cut = new ElementCountingRetestIdProvider();
		assertThat( cut.getRetestId( create( fromString( "/html[1]/div[1]" ), "div" ) ) ).isEqualTo( "div" );
		assertThat( cut.getRetestId( create( fromString( "/html[1]/div[1]" ), "div" ) ) ).isEqualTo( "div-1" );
		assertThat( cut.getRetestId( create( fromString( "/html[1]/div[1]/div[1]" ), "div" ) ) ).isEqualTo( "div-2" );
		assertThat( cut.getRetestId( create( fromString( "/html[1]/div[4]" ), "div" ) ) ).isEqualTo( "div-3" );
		assertThat( cut.getRetestId( create( fromString( "/html[1]/a[1]" ), "a" ) ) ).isEqualTo( "a" );
	}
}
