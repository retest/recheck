package de.retest.recheck.persistence.bin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;

import de.retest.recheck.persistence.IncompatibleReportVersionException;
import de.retest.recheck.util.VersionProvider;

class KryoPersistenceTest {

	@Test
	void roundtrip_should_work( @TempDir final Path temp ) throws IOException {
		final Path file = temp.resolve( "test.test" );
		Files.createFile( file );
		final URI identifier = file.toUri();

		final KryoPersistence<de.retest.recheck.test.Test> kryoPersistence = new KryoPersistence<>();
		final ArrayList<String> tests = new ArrayList<>();
		tests.add( "../test.test" );
		final de.retest.recheck.test.Test persisted = new de.retest.recheck.test.Test( tests );
		kryoPersistence.save( identifier, persisted );
		final de.retest.recheck.test.Test loaded = kryoPersistence.load( identifier );

		assertThat( persisted.getRelativeActionSequencePaths() ).isEqualTo( loaded.getRelativeActionSequencePaths() );
	}

	@Test
	void incompatible_version_should_give_persisting_version( @TempDir final Path temp ) throws IOException {
		final Path file = temp.resolve( "incompatible-test.test" );
		Files.createFile( file );
		final URI identifier = file.toUri();

		final KryoPersistence<de.retest.recheck.test.Test> kryoPersistence = new KryoPersistence<>();
		final ArrayList<String> tests = new ArrayList<>();
		tests.add( "../test.test" );
		kryoPersistence.save( identifier, new de.retest.recheck.test.Test( tests ) );

		final Kryo kryoMock = mock( Kryo.class );
		when( kryoMock.readClassAndObject( Mockito.any() ) ).thenThrow( KryoException.class );
		final KryoPersistence<de.retest.recheck.test.Test> differentKryoPersistence =
				new KryoPersistence<>( kryoMock, "old Version" );

		assertThatThrownBy( () -> differentKryoPersistence.load( identifier ) )
				.isInstanceOf( IncompatibleReportVersionException.class )
				.hasMessageContaining( "Incompatible recheck versions: report was written with "
						+ VersionProvider.RETEST_VERSION + ", but read with old Version." );
	}

}
