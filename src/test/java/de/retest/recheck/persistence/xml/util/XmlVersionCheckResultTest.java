package de.retest.recheck.persistence.xml.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.retest.recheck.persistence.xml.TestPersistable;

public class XmlVersionCheckResultTest {

	private XmlVersionCheckResult check;

	@Test
	public void is_compatible_with_correct_version() throws Exception {
		check = XmlVersionCheckResult.create( toStream( TestPersistable.XML ) );
		assertThat( check.isCompatible() ).isTrue();
	}

	@Test
	public void is_NOT_compatible_with_INcorrect_version() throws Exception {
		check = XmlVersionCheckResult.create( toStream( TestPersistable.XML_WITH_WRONG_VERSION ) );
		assertThat( check.isCompatible() ).isFalse();
	}

	@Test
	public void is_NOT_compatible_with_INcorrect_class_name() throws Exception {
		check = XmlVersionCheckResult.create( toStream( TestPersistable.XML_WITH_RENAMED_CLASS ) );
		assertThat( check.isCompatible() ).isFalse();
	}

	@Test
	public void creates_correct_object_from_stream() throws Exception {
		check = XmlVersionCheckResult.create( toStream( TestPersistable.XML ) );
		assertThat( check.oldVersion ).isEqualTo( TestPersistable.PERSISTENCE_VERSION );
	}

	@Test
	public void resets_stream_correctly() throws Exception {
		final BufferedInputStream stream = toStream( TestPersistable.XML );

		XmlVersionCheckResult.create( stream );

		assertThat( IOUtils.toString( stream, StandardCharsets.UTF_8 ) ).isEqualTo( TestPersistable.XML );
	}

	@Test( expected = IllegalArgumentException.class )
	public void empty_stream_throws_correct_ex() throws Exception {
		check = XmlVersionCheckResult.create( toStream( "" ) );
	}

	@Test( expected = IllegalArgumentException.class )
	public void broken_xml_stream_throws_correct_ex() throws Exception {
		check = XmlVersionCheckResult.create( toStream( "<?xml" ) );
	}

	@Test( expected = IllegalArgumentException.class )
	public void old_xml_throws_correct_ex() throws Exception {
		check = XmlVersionCheckResult
				.create( toStream( "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- retest version --><root>" ) );
	}

	public static BufferedInputStream toStream( final String xml ) {
		return new BufferedInputStream( IOUtils.toInputStream( xml, StandardCharsets.UTF_8 ) );
	}

}
