package de.retest.util;

import static de.retest.util.ObjectUtil.nextHashCode;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ObjectUtilTest {

	@Test
	public void nextHashCode_must_be_order_sensitive() throws Exception {
		assertThat( nextHashCode( nextHashCode( nextHashCode( 1, "you" ), "me" ), "them" ) )
				.isNotEqualTo( nextHashCode( nextHashCode( nextHashCode( 1, "them" ), "me" ), "you" ) );
	}

	@Test
	public void isObjectToString_should_be_recognized() throws Exception {
		assertThat( ObjectUtil.isObjectToString( new Object().toString() ) ).isTrue();
	}

	@Test
	public void removeObjectHash_should_be_removed() throws Exception {
		assertThat( ObjectUtil.removeObjectHash( new Object().toString() ) ).isEqualTo( "java.lang.Object" );
	}

	@Test
	public void isObjectToString_should_be_null_safe() {
		ObjectUtil.isObjectToString( null );
		// do not throw exception
	}

	@Test
	public void removeObjectHash_should_be_null_safe() {
		ObjectUtil.removeObjectHash( null );
		// do not throw exception
	}
}
