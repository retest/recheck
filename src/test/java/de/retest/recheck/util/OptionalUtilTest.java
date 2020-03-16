package de.retest.recheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptionalUtilTest {

	Object value;
	Optional<Object> present;
	Optional<Object> absent;

	@BeforeEach
	void setUp() throws Exception {
		value = new Object();
		present = Optional.of( value );
		absent = Optional.empty();
	}

	@Test
	void stream_should_contain_value_if_present() {
		assertThat( OptionalUtil.stream( present ) ).containsExactly( value );
	}

	@Test
	void stream_should_be_empty_if_absent() throws Exception {
		assertThat( OptionalUtil.stream( absent ) ).isEmpty();
	}

	@Test
	void or_should_return_given_optional_if_present() throws Exception {
		assertThat( OptionalUtil.or( present, () -> absent ) ).isEqualTo( present );
	}

	@Test
	void or_should_return_use_supplier_if_absent() throws Exception {
		assertThat( OptionalUtil.or( absent, () -> present ) ).isEqualTo( present );
	}

}
