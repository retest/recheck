import static de.retest.recheck.ignore.FilterLoader.load;
import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.retest.recheck.review.ignore.io.ErrorHandlingLoader;
import io.github.netmikey.logunit.api.LogCapturer;

public class DefaultRecheckIgnoreJsTest {

	@RegisterExtension
	LogCapturer warningAndErrorLogs = LogCapturer.create() //
			.captureForType( ErrorHandlingLoader.class );

	@Test
	void default_recheck_ignore_should_load_without_exception() throws Exception {
		load( get( getClass().getResource( "default-recheck.ignore.js" ).toURI() ) ).load();

		// Assert that no error was logged...
		assertThat( warningAndErrorLogs.size() ).isEqualTo( 0 );
	}

}
