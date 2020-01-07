package de.retest.recheck.ui.diff.meta;

import java.io.Serializable;

import lombok.Value;

@Value
public class MetadataElementDifference implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String key;
	private final String expected;
	private final String actual;
}
