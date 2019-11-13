import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.event.Level;

import de.retest.recheck.ignore.JSFilterImpl;
import io.github.netmikey.logunit.api.LogCapturer;

public class DefaultRecheckIgnoreJsTest {

	@RegisterExtension
	LogCapturer warningAndErrorLogs = LogCapturer.create() //
			.forLevel( Level.WARN ) //
			.captureForType( JSFilterImpl.class );

	@Test
	void default_recheck_ignore_should_load_without_exception() throws Exception {
		new JSFilterImpl( Paths.get( getClass().getResource( "default-recheck.ignore.js" ).toURI() ) );

		// Assert that no error was logged...
		assertThat( warningAndErrorLogs.size() ).isEqualTo( 0 );
	}

}
