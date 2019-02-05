package de.retest.recheck.image;

import static de.retest.recheck.ui.image.Screenshot.getPersistenceIdPrefix;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.image.Screenshot.ImageType;

public class ScreenshotTest {

	private static final String NULL_BYTE_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

	@Test
	public void persistenceIdPrefix_should_return_prefix() {
		final String prefix = "test2342";
		assertThat( new Screenshot( prefix, new byte[0], ImageType.PNG ).getPersistenceIdPrefix() ).isEqualTo( prefix );
	}

	@Test
	public void static_persistenceIdPrefix_should_return_prefix() {
		final String prefix = "test2342";
		final String persistenceId = new Screenshot( prefix, new byte[0], ImageType.PNG ).getPersistenceId();
		assertThat( Screenshot.getPersistenceIdPrefix( persistenceId ) ).isEqualTo( prefix );
	}

	@Test
	public void same_imagedata_shoud_create_same_persistenceId() {
		final byte[] image = new byte[0];
		final String persistenceId1 = new Screenshot( "", image, ImageType.PNG ).getPersistenceId();
		final String persistenceId2 = new Screenshot( "", image, ImageType.PNG ).getPersistenceId();

		assertThat( persistenceId2 ).isEqualTo( persistenceId1 );
	}

	@Test
	public void stable_persistenceId_regresion_test() {
		final String persistenceId = new Screenshot( "test", new byte[0], ImageType.PNG ).getPersistenceId();
		assertThat( persistenceId ).isEqualTo( "test_" + NULL_BYTE_SHA256 );
	}

	@Test
	public void persistenceId_contains_prefix() throws Exception {
		final String prefix = "test_prefix";
		final String persistenceId = new Screenshot( prefix, new byte[0], ImageType.PNG ).getPersistenceId();
		assertThat( persistenceId ).contains( prefix );
	}

	@Test( expected = RuntimeException.class )
	public void screenshot_throws_exeption_when_created_with_persistence_id() throws Exception {
		final Screenshot s = new Screenshot( "", new byte[0], ImageType.PNG );
		new Screenshot( s.getPersistenceId(), s.getBinaryData(), s.getType() );
	}

	@Test
	public void getPersistenceIdPrefix_should_return_only_prefix() throws Exception {
		assertThat( getPersistenceIdPrefix( "" ) ).isEqualTo( "" );
		assertThat( getPersistenceIdPrefix( "realsha_" + NULL_BYTE_SHA256 ) ).isEqualTo( "realsha" );
	}

}
