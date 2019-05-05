package de.retest.recheck;

import de.retest.recheck.ui.descriptors.idproviders.RetestIdProvider;
import de.retest.recheck.util.RetestIdProviderUtil;
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
	private final RetestIdProvider retestIdProvider = RetestIdProviderUtil.getConfiguredRetestIdProvider();

}
