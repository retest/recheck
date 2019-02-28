package de.retest.recheck;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RecheckOptions {

	@Builder.Default
	FileNamerStrategy fileNamerStrategy = new MavenConformFileNamerStrategy();

	@Builder.Default
	String suiteName = new MavenConformFileNamerStrategy().getTestClassName();

}
