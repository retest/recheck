package de.retest.recheck.meta.global;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import de.retest.recheck.meta.MetadataProvider;

public class TimeMetadataProvider implements MetadataProvider {

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

	public static final String TIME = "time.time";
	public static final String DATE = "time.date";
	public static final String ZONE = "time.zone";
	public static final String OFFSET = "time.offset";

	@Override
	public Map<String, String> retrieve() {
		final Map<String, String> map = new HashMap<>();

		final ZonedDateTime now = ZonedDateTime.now();
		final LocalTime time = now.toLocalTime();
		final LocalDate date = now.toLocalDate();
		final ZoneId zone = now.getZone();
		final ZoneOffset offset = now.getOffset();

		map.put( TIME, time.format( TIME_FORMATTER ) );
		map.put( DATE, date.format( DATE_FORMATTER ) );
		map.put( ZONE, zone.getId() );
		map.put( OFFSET, offset.getId() );

		return map;
	}
}
