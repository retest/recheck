package de.retest.recheck.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ConfigurationExceptionTest {

	@Test( expected = AssertionError.class )
	public void no_details_should_throw_AssertionError() throws Exception {
		new ConfigurationException( new Property( "me.to" ), (String) null );
	}

	@Test( expected = AssertionError.class )
	public void no_throwable_should_throw_AssertionError() throws Exception {
		new ConfigurationException( new Property( "me.to" ), (Throwable) null );
	}

	@Test( expected = AssertionError.class )
	public void no_throwable_and_details_should_throw_AssertionError() throws Exception {
		new ConfigurationException( new Property( "me.to" ), null, null );
	}

	@Test
	public void no_details_should_return_throwable_class_and_msg() throws Exception {
		assertThat( new ConfigurationException( new Property( "me.to" ), null, new RuntimeException() ).getDetails() )
				.isEqualTo( "java.lang.RuntimeException: null" );
		assertThat( new ConfigurationException( new Property( "me.to" ), null, new RuntimeException( "Baaad!" ) )
				.getDetails() ).isEqualTo( "java.lang.RuntimeException: Baaad!" );
	}

	@Test
	public void no_throwable_should_return_only_details() throws Exception {
		assertThat( new ConfigurationException( new Property( "me.to" ), "soo detailed", null ).getDetails() )
				.isEqualTo( "soo detailed" );
	}

	@Test
	public void details_should_contain_both_given_details_and_thowable_class_and_msg() throws Exception {
		assertThat( new ConfigurationException( new Property( "me.to" ), "soo detailed", new RuntimeException() )
				.getDetails() ).isEqualTo( "soo detailed (java.lang.RuntimeException: null)" );
		assertThat(
				new ConfigurationException( new Property( "me.to" ), "soo detailed", new RuntimeException( "Baaad" ) )
						.getDetails() ).isEqualTo( "soo detailed (java.lang.RuntimeException: Baaad)" );
	}
}
