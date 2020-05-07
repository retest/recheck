package de.retest.recheck.persistence;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportUploadContainer {

	private final String reportName;
	private final byte[] data;
	private final String uploadUrl;
}
