package de.retest.persistence;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.time.FastDateFormat;

public class DateAdapter extends XmlAdapter<String, Date> {

	private static final FastDateFormat defaultDateFormat =
			FastDateFormat.getInstance( "yyyy-MM-dd'T'HH:mm:ssZZ", TimeZone.getTimeZone( "UTC" ) );

	private static final FastDateFormat[] possibleDateFormats = { defaultDateFormat, //
			FastDateFormat.getInstance( "yyyy-MM-dd HH:mm:ss Z" ), // "dateFormat" before refactoring
			FastDateFormat.getInstance( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" ), // "oldDateFormat" before refactoring
			FastDateFormat.getInstance( "yyyy-MM-dd'T'HH:mm:ss.SSSZZ" ), // format exists in tests, likely this is the
																			// correct for for "oldDateFormat"
	};

	@Override
	public String marshal( final Date value ) {
		if ( value == null ) {
			return null;
		}
		return defaultDateFormat.format( value );
	}

	@Override
	public Date unmarshal( final String value ) {
		if ( value == null ) {
			return null;
		}
		for ( final FastDateFormat dateFormat : possibleDateFormats ) {
			try {
				return dateFormat.parse( value );
			} catch ( final ParseException ignored ) {/* NOP */}
		}
		throw new RuntimeException( "Can't parse date '" + value + "'!" );
	}
}
