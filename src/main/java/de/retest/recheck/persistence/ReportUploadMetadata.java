package de.retest.recheck.persistence;

import java.net.URI;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportUploadMetadata {
	private final URI location;
	private final String uploadUrl;
	private final List<String> testClasses;
}
