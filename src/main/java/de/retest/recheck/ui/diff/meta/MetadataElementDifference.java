package de.retest.recheck.ui.diff.meta;

import lombok.Value;

@Value
public class MetadataElementDifference {

	private final String key;
	private final String expected;
	private final String actual;
}
