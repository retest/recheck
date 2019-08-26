package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.persistence.FileNamer;

@RunWith( PowerMockRunner.class )
@PrepareForTest( RecheckIgnoreUtil.class )
public class RecheckOptionsTest {

	@Test
	public void should_reuse_file_namer_strategy_for_suite_name() throws Exception {
		mockStatic( RecheckIgnoreUtil.class );
		PowerMockito
				.when( RecheckIgnoreUtil.class,
						method( RecheckIgnoreUtil.class, "loadRecheckSuiteIgnore", File.class ) )
				.withArguments( any() ).thenReturn( null );

		final FileNamerStrategy fileNamerStrategy = mock( FileNamerStrategy.class );

		final FileNamer fileNamer = mock( FileNamer.class );
		when( fileNamerStrategy.createFileNamer( any() ) ).thenReturn( fileNamer );
		when( fileNamerStrategy.getTestClassName() ).thenReturn( "SOME_SPECIAL_VALUE" );

		final RecheckOptions cut = RecheckOptions.builder() //
				.fileNamerStrategy( fileNamerStrategy ) //
				.build();

		assertThat( cut.getSuiteName() ).isEqualTo( fileNamerStrategy.getTestClassName() );
	}

	@Test
	public void should_use_suite_name_if_set() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.suiteName( "bar" ) //
				.build();
		assertThat( cut.getSuiteName() ).isEqualTo( "bar" );
	}

}
