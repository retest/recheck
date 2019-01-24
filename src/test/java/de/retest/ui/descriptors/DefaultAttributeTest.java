package de.retest.ui.descriptors;

import static de.retest.ui.descriptors.DefaultAttribute.parameterTypeAttribute;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DefaultAttributeTest {

	@Test
	public void parse_is_not_supported_and_should_return_null() throws Exception {
		assertThat( parameterTypeAttribute.parse( "value" ) ).isNull();
	}

}
