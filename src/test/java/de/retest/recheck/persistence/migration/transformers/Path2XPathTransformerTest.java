package de.retest.recheck.persistence.migration.transformers;

import static com.google.common.base.Charsets.UTF_8;
import static de.retest.recheck.persistence.migration.transformers.Path2XPathTransformer.toXPath;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.retest.recheck.persistence.migration.XmlTransformer;
import de.retest.recheck.persistence.migration.transformers.Path2XPathTransformer;
import de.retest.recheck.util.ApprovalsUtil;

public class Path2XPathTransformerTest {

	@Test
	public void test_toXPath() {
		assertThat( toXPath( "Window_0" ) ).isEqualTo( "Window[1]" );
		assertThat( toXPath( "Window_0/RootPane_0" ) ).isEqualTo( "Window[1]/RootPane[1]" );

		assertThat( toXPath( "Window_0/RootPane_0/JPanel_103429857" ) )
				.isEqualTo( "Window[1]/RootPane[1]/JPanel[103429858]" );

		assertThat( toXPath( "Window_0/RootPane_0/TabbedPaneDemo$1_1" ) )
				.isEqualTo( "Window[1]/RootPane[1]/TabbedPaneDemo$1[2]" );

		assertThat( toXPath( "Window_0/RootPane_0/TabbedPaneDemo$1_1/JButton_0" ) )
				.isEqualTo( "Window[1]/RootPane[1]/TabbedPaneDemo$1[2]/JButton[1]" );

		assertThat( toXPath( "Window_0/JRootPane_0/JLayeredPane_0/popup_JPanel/JPopupMenu_0/JMenuItem_1" ) )
				.isEqualTo( "Window[1]/JRootPane[1]/JLayeredPane[1]/popup_JPanel[1]/JPopupMenu[1]/JMenuItem[2]" );
	}

	@Test
	public void path_should_be_transformed() throws Exception {
		final InputStream inputStream =
				new FileInputStream( new File( "src/test/resources/migration/Path2XPathTransformer.xml" ) );

		final XmlTransformer transformer = new Path2XPathTransformer();
		final InputStream transform = transformer.transform( inputStream );
		ApprovalsUtil.verifyXml( IOUtils.toString( transform, UTF_8.name() ) );
	}
}
