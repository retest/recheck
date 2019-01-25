package de.retest.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import de.retest.util.RetestIdUtil.InvalidRetestIdException;

class RetestIdUtilTest {

	@Test
	void weired_chars_should_be_removed_when_normalized() {
		final String rawId = " this. (should) \n be -/ \\ +NORMALIZEDé{}";
		final String normalizedId = RetestIdUtil.normalize( rawId );
		assertThat( normalizedId ).isEqualTo( "this_should_be_normalized" );
	}

	@Test
	void too_long_single_word_ids_should_be_cut() {
		final String rawId = "supercalifragilisticexpialidocious";
		final String cutId = RetestIdUtil.cut( rawId );
		assertThat( cutId.length() ).isLessThan( 20 );
		assertThat( cutId ).isEqualTo( "supercalifragil" );
	}

	@Test
	void too_long_multi_word_ids_should_be_normalized_and_cut_sensibly() {
		final String rawId =
				"This       is\tsome     very long_sentence, \nthat    could be in a link text, or in some paragraph, and really is to long to be used as id.";
		final String normalizedAndCutId = RetestIdUtil.normalizeAndCut( rawId );
		assertThat( normalizedAndCutId.length() ).isLessThan( 20 );
		assertThat( normalizedAndCutId ).isEqualTo( "this_is_some" );
	}

	@Test
	void null_ids_should_be_invalid() throws Exception {
		final String nullId = null;
		assertThat( RetestIdUtil.isValid( nullId ) ).isFalse();
		assertThatThrownBy( () -> RetestIdUtil.validate( nullId, null ) ) //
				.isInstanceOf( InvalidRetestIdException.class ) //
				.hasMessage( "retest ID must not be null for null" );
	}

	@Test
	void empty_ids_should_be_invalid() throws Exception {
		final String nullId = "";
		assertThat( RetestIdUtil.isValid( nullId ) ).isFalse();
		assertThatThrownBy( () -> RetestIdUtil.validate( nullId, null ) ) //
				.isInstanceOf( InvalidRetestIdException.class ) //
				.hasMessage( "retest ID must not be empty for null" );
	}

	@Test
	void whitespace_ids_should_be_invalid() throws Exception {
		final String nullId = " ";
		assertThat( RetestIdUtil.isValid( nullId ) ).isFalse();
		assertThatThrownBy( () -> RetestIdUtil.validate( nullId, null ) ) //
				.isInstanceOf( InvalidRetestIdException.class ) //
				.hasMessage( "retest ID must not contain any whitespaces or special characters for null" );
	}

	@Test
	void special_character_ids_should_be_invalid() throws Exception {
		final String nullId = "+";
		assertThat( RetestIdUtil.isValid( nullId ) ).isFalse();
		assertThatThrownBy( () -> RetestIdUtil.validate( nullId, null ) ) //
				.isInstanceOf( InvalidRetestIdException.class ) //
				.hasMessage( "retest ID must not contain any whitespaces or special characters for null" );
	}

	@Test
	void normalize_null_id_should_return_empty_string() {
		assertThat( RetestIdUtil.normalize( null ) ).isEqualTo( "" );
	}

	@Test
	void normalize_empty_id_should_return_empty_string() {
		assertThat( RetestIdUtil.normalize( "" ) ).isEqualTo( "" );
	}

	@Test
	void cut_null_id_should_return_empty_string() {
		assertThat( RetestIdUtil.cut( null ) ).isEqualTo( "" );
	}

	@Test
	void cut_empty_id_should_return_empty_string() {
		assertThat( RetestIdUtil.cut( "" ) ).isEqualTo( "" );
	}

	@Test
	void type_qualifier_should_be_cut() {
		assertThat( RetestIdUtil.cutTypeQualifier( "javax.swing.Box$Filler" ) ).isEqualTo( "Box$Filler" );
		assertThat( RetestIdUtil.cutTypeQualifier( "Box$Filler" ) ).isEqualTo( "Box$Filler" );
		assertThat( RetestIdUtil.cutTypeQualifier( null ) ).isEqualTo( "" );
		assertThat( RetestIdUtil.cutTypeQualifier( "" ) ).isEqualTo( "" );
	}
}
