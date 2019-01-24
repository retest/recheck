package de.retest.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

public class DateAdapterTest {

	DateAdapter dateAdapter;

	@Before
	public void setUp() throws Exception {
		dateAdapter = new DateAdapter();
	}

	@Test
	public void roundtrip_with_default_format_works() {
		final Date origin = new Date();
		final String string = dateAdapter.marshal( origin );
		assertThat( dateAdapter.unmarshal( string ) ).isInSameSecondAs( origin );
	}

	@Test
	public void can_parse_string_from_tests() {
		assertThat( dateAdapter.unmarshal( "2016-09-14T12:52:13.436+02:00" ) ).isNotNull();
	}

	@Test
	public void can_parse_default_dateFormat_from_before_refactoring() {
		final Date origin = new Date();
		final SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z", Locale.GERMANY );
		dateFormat.setTimeZone( TimeZone.getTimeZone( "GMT+2" ) );
		final String defaultdateFormatBeforeRefactoring = dateFormat.format( origin );

		assertThat( dateAdapter.unmarshal( defaultdateFormatBeforeRefactoring ) ).isInSameSecondAs( origin );
	}

	@Test
	public void can_parse_oldDateFormat_from_before_refactoring() {
		final Date origin = new Date();
		final String oldDateFormatBeforeRefactoring =
				new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" ).format( origin );

		assertThat( dateAdapter.unmarshal( oldDateFormatBeforeRefactoring ) ).isInSameSecondAs( origin );
	}

}
