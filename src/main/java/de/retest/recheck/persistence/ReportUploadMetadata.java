package de.retest.recheck.persistence;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportUploadMetadata {
	private final byte[] data;
	private final String uploadUrl;
	private final List<String> testClasses;
}
