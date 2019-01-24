package de.retest.ui.descriptors;

import static de.retest.ui.descriptors.PathAttribute.parameterTypePath;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.retest.ui.Path;
import de.retest.ui.PathElement;

public class PathAttributeTest {

	@Test
	public void parse_should_parse_path_correctly() throws Exception {
		final Path parent = Path.path( new PathElement( "parent" ) );
		final Path path = Path.path( parent, new PathElement( "path" ) );
		final Path test = Path.path( path, new PathElement( "test" ) );

		assertThat( parameterTypePath.parse( "parent/path/test" ) ).isEqualTo( test );
	}

}
