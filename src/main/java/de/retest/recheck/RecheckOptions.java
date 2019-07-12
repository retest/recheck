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
	private final String suiteName = null;

	@Builder.Default
	private boolean reportUploadEnabled = false;

	public String getSuiteName() {
		return suiteName == null ? fileNamerStrategy.getTestClassName() : suiteName;
	}

}
