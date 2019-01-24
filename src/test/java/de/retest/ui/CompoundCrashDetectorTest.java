package de.retest.ui;

import org.junit.Test;
import org.mockito.Mockito;

import de.retest.ui.actions.ExceptionWrapper;

public class CompoundCrashDetectorTest {

	@Test
	public void compound_should_call_all_given_detectors_one_after_another() {
		final CrashDetector detector1 = Mockito.mock( CrashDetector.class );
		Mockito.when( detector1.hasCrashed() ).thenReturn( true );
		Mockito.when( detector1.getCrashCause() ).thenReturn( Mockito.mock( ExceptionWrapper.class ) );
		final CrashDetector detector2 = Mockito.mock( CrashDetector.class );
		Mockito.when( detector2.hasCrashed() ).thenReturn( true );
		final CompoundCrashDetector compound = new CompoundCrashDetector( detector1, detector2 );
		compound.hasCrashed();
		compound.getCrashCause();
		Mockito.verify( detector1 ).hasCrashed();
		Mockito.verify( detector1 ).getCrashCause();
		Mockito.verifyZeroInteractions( detector2 );
		Mockito.when( detector1.hasCrashed() ).thenReturn( false );
		Mockito.when( detector1.getCrashCause() ).thenReturn( null );
		compound.hasCrashed();
		compound.getCrashCause();
		Mockito.verify( detector2 ).hasCrashed();
		Mockito.verify( detector2 ).getCrashCause();
	}
}
