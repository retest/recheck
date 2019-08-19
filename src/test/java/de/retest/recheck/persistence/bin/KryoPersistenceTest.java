package de.retest.recheck.persistence.bin;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KryoPersistenceTest {

	@Test
	void test( @TempDir final Path temp ) throws IOException {
		final Path file = temp.resolve( "file.report" );
		Files.createFile( file );
		final URI identifier = file.toUri();

		final KryoPersistence<de.retest.recheck.test.Test> kryoPersistence = new KryoPersistence<>();
		kryoPersistence.save( identifier, new de.retest.recheck.test.Test( Arrays.asList( "../test.test" ) ) );
		kryoPersistence.load( identifier );
	}

}
