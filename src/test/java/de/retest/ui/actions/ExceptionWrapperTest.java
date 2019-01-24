package de.retest.ui.actions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { ExceptionWrapper.class, NullPointerException.class } )
public class ExceptionWrapperTest {

	@Test
	public void toString_should_account_for_null_msg() {
		final NullPointerException npe = new NullPointerException();
		assertThat( new ExceptionWrapper( npe ).toString() ).isEqualTo(
				"java.lang.NullPointerException at de.retest.ui.actions.ExceptionWrapperTest.toString_should_account_for_null_msg(ExceptionWrapperTest.java:19)" );
	}

	@Test
	public void should_consider_deepest_cause() {
		final NullPointerException npe = new NullPointerException( "Boohoo!" );
		final RuntimeException exc1 = new RuntimeException( "Something bad happened", npe );
		final RuntimeException exc2 = new RuntimeException( "Oh, not good!", exc1 );
		assertThat( new ExceptionWrapper( exc2 ).toString() ).isEqualTo(
				"java.lang.NullPointerException: Boohoo! at de.retest.ui.actions.ExceptionWrapperTest.should_consider_deepest_cause(ExceptionWrapperTest.java:26)" );
	}

	@Test
	public void toString_should_account_for_null_trace() {
		MockGateway.MOCK_GET_CLASS_METHOD = true;
		final NullPointerException npe = PowerMockito.mock( NullPointerException.class );
		PowerMockito.doReturn( NullPointerException.class ).when( npe ).getClass();
		PowerMockito.when( npe.getStackTrace() ).thenReturn( null );
		assertThat( new ExceptionWrapper( npe ).toString() ).isEqualTo( "java.lang.NullPointerException" );
	}

	@After
	public void tearDown() {
		MockGateway.MOCK_GET_CLASS_METHOD = false;
	}
}
