package de.retest.recheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class OptionalUtilTest {

	@Test
	void stream_should_contain_value_if_present() {
		final Object obj = new Object();
		final Optional<Object> present = Optional.of( obj );
		assertThat( OptionalUtil.stream( present ) ).containsExactly( obj );
	}

	@Test
	void stream_should_be_empty_if_absent() throws Exception {
		final Optional<Object> absent = Optional.empty();
		assertThat( OptionalUtil.stream( absent ) ).isEmpty();
	}

	@Test
	void or_should_return_given_optional_if_present() throws Exception {
		final Optional<Object> opt = Optional.of( new Object() );
		final Supplier<Optional<Object>> supplier = () -> Optional.empty();
		assertThat( OptionalUtil.or​( opt, supplier ) ).isEqualTo( opt );
	}

	@Test
	void or_should_return_use_supplier_if_absent() throws Exception {
		final Optional<Object> absent = Optional.empty();
		final Optional<Object> present = Optional.of( new Object() );
		final Supplier<Optional<Object>> supplier = () -> present;
		assertThat( OptionalUtil.or​( absent, supplier ) ).isEqualTo( present );
	}

}
