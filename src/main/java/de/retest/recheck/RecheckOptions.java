package de.retest.recheck;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RecheckOptions {

	@Builder.Default
	private FileNamerStrategy fileNamerStrategy = new MavenConformFileNamerStrategy();

	@Builder.Default
	private String suiteName = new MavenConformFileNamerStrategy().getTestClassName();

	@Builder.Default
	private double maxPixelDiff = 0.0;

}
