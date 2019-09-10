package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;

public class LegacyCompatibilityFileNamerStrategyTest {

	private static class LegacyFileNamerStrategy implements FileNamerStrategy {

		private final Path root;

		public LegacyFileNamerStrategy( final Path root ) throws IOException {
			this.root = root;
		}

		@Override
		public FileNamer createFileNamer( final String... baseNames ) {
			return new FileNamer() {

				@Override
				public File getFile( final String extension ) {
					return resolveRoot( baseNames, extension );
				}

				@Override
				public File getResultFile( final String extension ) {
					return resolveRoot( baseNames, extension );
				}
			};
		}

		private File resolveRoot( final String[] baseNames, final String extension ) {
			final int last = baseNames.length - 1;
			final List<String> list = new ArrayList<>( Arrays.asList( baseNames ) );
			list.set( last, baseNames[last] + extension );

			Path path = root;
			for ( final String sub : list ) {
				path = path.resolve( sub );
			}

			return path.toFile();
		}
	}

	@Test
	void default_test_class_name_should_be_implemented( @TempDir final Path tmp ) throws Exception {
		final Recheck re = new RecheckImpl(
				RecheckOptions.builder().fileNamerStrategy( new LegacyFileNamerStrategy( tmp ) ).build() );
		re.check( "String", new StringRecheckAdapter(), "stepName" );

		assertThat( tmp.resolve(
				"de.retest.recheck.LegacyCompatibilityFileNamerStrategyTest/default_test_class_name_should_be_implemented.stepName.recheck" ) )
						.exists();
		try {
			re.capTest();
			Assert.fail();
		} catch ( final AssertionError e ) {//expected
		}

		re.startTest( "default_test_class_name_should_be_implemented" );
		re.check( "String", new StringRecheckAdapter(), "stepName" );
		re.capTest();
		re.cap();

		assertThat( tmp.resolve( "de.retest.recheck.LegacyCompatibilityFileNamerStrategyTest.report" ) ).exists();
		assertThat( tmp.resolve( "tests.report" ) ).exists();
	}

	private static class StringRecheckAdapter implements RecheckAdapter {

		@Override
		public DefaultValueFinder getDefaultValueFinder() {
			return null;
		}

		@Override
		public Set<RootElement> convert( final Object toVerify ) {
			final RootElement result = new RootElement( "retestId",
					IdentifyingAttributes.create( de.retest.recheck.ui.Path.fromString( "root" ), "String" ),
					new Attributes(), null, "screen", -1, "title" );
			return Collections.singleton( result );
		}

		@Override
		public boolean canCheck( final Object toVerify ) {
			return false;
		}
	}
}
