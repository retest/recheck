package de.retest.recheck;

import static de.retest.recheck.RecheckProperties.FILE_OUTPUT_FORMAT_PROPERTY_KEY;
import static de.retest.recheck.RecheckProperties.REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY;
import static de.retest.recheck.persistence.FileOutputFormat.CLOUD;
import static de.retest.recheck.persistence.FileOutputFormat.KRYO;
import static de.retest.recheck.persistence.FileOutputFormat.PLAIN;
import static org.assertj.core.api.Assertions.assertThat;

import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RecheckPropertiesTest {

	RecheckProperties cut;

	@BeforeEach
	void setUp() throws Exception {
		cut = ConfigFactory.create( RecheckProperties.class );
	}

	@Test
	void report_output_format_should_default_to_kryo() {
		assertThat( cut.getReportOutputFormat() ).isEqualTo( KRYO );
	}

	@Test
	void report_output_format_should_use_cloud_when_rehub_is_enabled() {
		cut.setProperty( REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY, "true" );

		assertThat( cut.getReportOutputFormat() ).isEqualTo( CLOUD );
	}

	@Test
	void state_output_format_should_default_to_plain() {
		assertThat( cut.getStateOutputFormat() ).isEqualTo( PLAIN );
	}

	@ParameterizedTest
	@ValueSource( strings = { "KRYO", "CLOUD" } )
	void state_output_format_should_use_plain_when_format_is_not_supported( final String unsupportedFormat ) {
		cut.setProperty( FILE_OUTPUT_FORMAT_PROPERTY_KEY, unsupportedFormat );

		assertThat( cut.getStateOutputFormat() ).isEqualTo( PLAIN );
	}

}
