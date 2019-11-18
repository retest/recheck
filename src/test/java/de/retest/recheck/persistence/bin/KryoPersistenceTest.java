package de.retest.recheck.persistence.bin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;

import de.retest.recheck.persistence.IncompatibleReportVersionException;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.util.VersionProvider;

class KryoPersistenceTest {

	@Test
	void roundtrip_should_work( @TempDir final Path temp ) throws IOException {
		final Path file = temp.resolve( "test.test" );
		Files.createFile( file );
		final URI identifier = file.toUri();

		final KryoPersistence<de.retest.recheck.test.Test> kryoPersistence = new KryoPersistence<>();
		final de.retest.recheck.test.Test persisted = createDummyTest();
		kryoPersistence.save( identifier, persisted );
		final de.retest.recheck.test.Test loaded = kryoPersistence.load( identifier );

		assertThat( persisted.getRelativeActionSequencePaths() ).isEqualTo( loaded.getRelativeActionSequencePaths() );
	}

	public de.retest.recheck.test.Test createDummyTest() {
		final ArrayList<String> tests = new ArrayList<>();
		tests.add( "../test.test" );
		return new de.retest.recheck.test.Test( tests );
	}

	@Test
	void incompatible_version_should_give_persisting_version( @TempDir final Path temp ) throws IOException {
		final Path file = temp.resolve( "incompatible-test.test" );
		Files.createFile( file );
		final URI identifier = file.toUri();

		final KryoPersistence<de.retest.recheck.test.Test> kryoPersistence = new KryoPersistence<>();
		kryoPersistence.save( identifier, createDummyTest() );

		final Kryo kryoMock = mock( Kryo.class );
		when( kryoMock.readClassAndObject( any() ) ).thenThrow( KryoException.class );
		final KryoPersistence<de.retest.recheck.test.Test> differentKryoPersistence =
				new KryoPersistence<>( kryoMock, "old Version" );

		assertThatThrownBy( () -> differentKryoPersistence.load( identifier ) )
				.isInstanceOf( IncompatibleReportVersionException.class )
				.hasMessageContaining( "Incompatible recheck versions: report was written with "
						+ VersionProvider.RETEST_VERSION + ", but read with old Version." );
	}

	@Test
	void unknown_version_should_give_correct_error() throws IOException {
		final Path file = Paths.get( "src/test/resources/de/retest/recheck/persistence/bin/old.report" );
		final URI identifier = file.toUri();

		final KryoPersistence<TestReport> differentKryoPersistence = new KryoPersistence<>();

		assertThatThrownBy( () -> differentKryoPersistence.load( identifier ) ) //
				.isInstanceOf( IncompatibleReportVersionException.class ) //
				.hasMessageContaining(
						"Incompatible recheck versions: report was written with an old recheck version (pre 1.5.0), but read with "
								+ VersionProvider.RETEST_VERSION + "." );
	}

	@Test
	void on_error_file_should_be_deleted() throws IOException {
		final File nonexistent = new File( "nonexistent.report" );
		final Kryo kryoMock = mock( Kryo.class );
		doThrow( KryoException.class ).when( kryoMock ).writeClassAndObject( any(), any() );
		final KryoPersistence<de.retest.recheck.test.Test> persistence =
				new KryoPersistence<>( kryoMock, "some version" );
		assertThatThrownBy( () -> persistence.save( nonexistent.toURI(), createDummyTest() ) )
				.isInstanceOf( KryoException.class );
		assertThat( nonexistent ).doesNotExist();
	}
}
