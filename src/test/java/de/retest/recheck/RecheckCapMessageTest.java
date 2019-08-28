package de.retest.recheck;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.util.ApprovalsUtil;

class RecheckCapMessageTest {

	@Test
	void no_golden_master_message_should_be_formatted_properly() throws Exception {
		final ActionReplayResult actionReplayResult0 = mock( NoGoldenMasterActionReplayResult.class );
		when( actionReplayResult0.getGoldenMasterPath() ).thenReturn( "/gm/path/0" );

		final ActionReplayResult actionReplayResult1 = mock( NoGoldenMasterActionReplayResult.class );
		when( actionReplayResult1.getGoldenMasterPath() ).thenReturn( "/gm/path/0" );

		final TestReplayResult testReplayResult = new TestReplayResult( "test-name", 1 );
		testReplayResult.addAction( actionReplayResult0 );
		testReplayResult.addAction( actionReplayResult1 );

		final RecheckCapMessage cut = new RecheckCapMessage( "SomeSuite", testReplayResult, null, null );

		ApprovalsUtil.verify( cut );
	}
}
