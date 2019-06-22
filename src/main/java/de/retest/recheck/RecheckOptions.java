package de.retest.recheck;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class RecheckOptions {

	@Builder.Default
	private final FileNamerStrategy fileNamerStrategy = new MavenConformFileNamerStrategy();

	@Builder.Default
	private final String suiteName = new MavenConformFileNamerStrategy().getTestClassName();

	@Builder.Default
	private final boolean rehubEnabled = false;

}
