package de.retest.recheck.persistence.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.util.VersionProvider;

public class ReTestXmlDataContainerTest {

	ReTestXmlDataContainer<Persistable> container;

	@Test
	public void container_contains_data_and_correct_type() throws Exception {
		final Persistable data = new TestPersistable();

		container = new ReTestXmlDataContainer<>( data );

		assertThat( container.data() ).isEqualTo( data );
		assertThat( container.dataType ).isEqualTo( TestPersistable.CLASS_NAME );
	}

	@Test
	public void container_contains_data_version() throws Exception {
		final int dataVersion = 2342;
		final Persistable data = mock( Persistable.class );
		when( data.version() ).thenReturn( dataVersion );

		container = new ReTestXmlDataContainer<>( data );

		assertThat( container.dataTypeVersion ).isEqualTo( dataVersion );
	}

	@Test
	public void container_contains_retest_version() throws Exception {
		container = new ReTestXmlDataContainer<>( mock( Persistable.class ) );

		assertThat( container.reTestVersion ).isEqualTo( VersionProvider.RECHECK_VERSION );
	}

	@Test
	public void verify_field_constants_match_real_fields() throws Exception {
		ReTestXmlDataContainer.class.getDeclaredField( ReTestXmlDataContainer.DATA_TYPE_FIELD );
		ReTestXmlDataContainer.class.getDeclaredField( ReTestXmlDataContainer.DATA_TYPE_VERSION_FIELD );
	}
}
