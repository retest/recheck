package de.retest.recheck.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.Chars;
import net.jqwik.api.constraints.NumericChars;

class StringSimilarityTest {

	private static final Logger logger = LoggerFactory.getLogger( StringSimilarityTest.class );

	@Test
	void small_change_in_short_string_should_result_in_bigger_difference() {
		final String s0 = "abc";
		final String s1 = "adc";
		final String s2 = "abcd";
		final String s3 = "adcd";

		final double similarityPath0And1 = StringSimilarity.textSimilarity( s0, s1 );
		final double similarityPath2And3 = StringSimilarity.textSimilarity( s2, s3 );

		assertThat( similarityPath0And1 ).isCloseTo( 0.08, within( 0.01 ) );
		assertThat( similarityPath2And3 ).isCloseTo( 0.125, within( 0.01 ) );
	}

	@Test
	void pathSimilarity_should_be_percentage_of_difference() {
		final String commonPrefix =
				"Window/JRootPane_0/JLayeredPane_0/JPanel_0/TabbedPane_0/Tab_1/ExtendedPanel_0/JXPanel_0/JXLayer_0/Splitpane_0/JXLayer_0/JXPanel_0/JXLayer_0/TabbedModuleForm_0/TabbedPaneExt_0/Tab_0/FormAdapter_0/JPanel_0/GUI_BE";
		final String commonSuffix = "/Betriebename_0/JXPanel_0";
		final String difference = "_07_01_Betriebe_Stammdaten_0";
		final String path0 = commonPrefix + difference + commonSuffix;
		final String path1 = commonPrefix + commonSuffix;

		final double similarity = StringSimilarity.pathSimilarity( path0, path1 );

		final double expectedSimilarity = (commonPrefix.length() + commonSuffix.length())
				/ (double) (commonPrefix.length() + commonSuffix.length() + difference.length());
		assertThat( similarity ).isCloseTo( expectedSimilarity * expectedSimilarity, within( 0.01 ) );
	}

	@Property
	void pathSimilarity_should_always_be_between_0_and_1( @ForAll @RandomPath final String randomPath0,
			@ForAll @RandomPath final String randomPath1 ) throws Exception {
		final double similarity01 = StringSimilarity.pathSimilarity( randomPath0, randomPath1 );
		final double similarity10 = StringSimilarity.pathSimilarity( randomPath1, randomPath0 );

		assertThat( similarity01 ).as( "'%s' compared to '%s'", randomPath0, randomPath1 ).isBetween( 0.0, 1.0 );
		assertThat( similarity10 ).as( "'%s' compared to '%s'", randomPath1, randomPath0 ).isBetween( 0.0, 1.0 );
	}

	@Property
	void pathSimilarity_for_equal_paths_should_always_return_1( @ForAll @RandomPath final String randomPath )
			throws Exception {
		assertThat( StringSimilarity.pathSimilarity( randomPath, randomPath ) )
				.as( "'%s' compared to itself", randomPath ).isEqualTo( 1.0 );
	}

	@Test
	void pathSimilarity_should_handle_null_values() throws Exception {
		final String s0 = null;
		final String s1 = "a";

		final double similarity00 = StringSimilarity.pathSimilarity( s0, s0 );
		final double similarity01 = StringSimilarity.pathSimilarity( s0, s1 );
		final double similarity10 = StringSimilarity.pathSimilarity( s1, s0 );

		assertThat( similarity00 ).isEqualTo( 1.0 );
		assertThat( similarity01 ).isEqualTo( 0.0 );
		assertThat( similarity10 ).isEqualTo( 0.0 );
	}

	@Test
	void one_change_should_be_more_similar_than_two_changes() throws Exception {
		final String orig =
				"Window/JRootPane_0/JLayeredPane_0/StatefulTableDemo_0/JTabbedPane_0/Tab_0/JPanel_0/JTable_0/row_2/column_4";
		final String oneChange =
				"Window/JRootPane_0/JLayeredPane_0/StatefulTableDemo_0/JTabbedPane_0/Tab_1/JPanel_0/JTable_0/row_2/column_4";
		final String twoChanges =
				"Window/JRootPane_0/JLayeredPane_0/StatefulTableDemo_0/JTabbedPane_0/Tab_1/JPanel_0/JTable_0/row_2/column_0";

		final double similarityWithOneChange = StringSimilarity.pathSimilarity( orig, oneChange );
		final double similarityWithTwoChanges = StringSimilarity.pathSimilarity( orig, twoChanges );

		assertThat( similarityWithOneChange ).isGreaterThan( similarityWithTwoChanges );
	}

	@Test
	void pathSimilarity_should_handle_common_prefix_and_suffix() throws Exception {
		final String s0 = "a";
		final String s1 = "aa";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isCloseTo( 0.5 * 0.5, within( 0.01 ) );
	}

	@Test
	void pathSimilarity_should_handle_common_prefix() throws Exception {
		final String s0 = "a";
		final String s1 = "ab";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isCloseTo( 0.5 * 0.5, within( 0.01 ) );
	}

	@Test
	void pathSimilarity_should_handle_common_suffix() throws Exception {
		final String s0 = "b";
		final String s1 = "ab";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isCloseTo( 0.5 * 0.5, within( 0.01 ) );
	}

	@Test
	void pathSimilarity_for_completely_different_paths_should_return_0() throws Exception {
		final String s0 = "a";
		final String s1 = "b";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isEqualTo( 0.0 );
	}

	@Test
	@Disabled( "This test can be performed manually to test other similarity implementations." )
	void pathSimilarity_should_be_efficient() {
		// Measurements taken on Jeremy's machine (2,5 GHz Intel Core i7, 16 GB 1600 MHz DDR3).
		// for StringUtils.getLevenshteinDistance( path0, path1, (int) maxLength / 2 ): 112595ms
		// for StringUtils.getLevenshteinDistance( path0, path1, (int) minLength / 2 ): 104451ms
		// for StringUtils.getJaroWinklerDistance( path0, path1 ): 78468ms
		// for StringUtils.getFuzzyDistance( path0, path1, Locale.GERMAN ): 3344ms
		// for pathSimilarity(path0, path1): 922ms
		final String path0 =
				"Window/JRootPane_0/JLayeredPane_0/JPanel_0/TabbedPane_0/Tab_1/ExtendedPanel_0/JXPanel_0/JXLayer_0/Splitpane_0/JXLayer_0/JXPanel_0/JXLayer_0/TabbedModuleForm_0/TabbedPaneExt_0/Tab_0/FormAdapter_0/JPanel_0/GUI_BE_07_01_Betriebe_Stammdaten_0/Betriebename_0/JXPanel_0";
		final String path1 =
				"Window/JRootPane_0/JLayeredPane_0/JPanel_0/TabbedPane_0/Tab_1/ExtendedPanel_0/JXPanel_0/JXLayer_0/Splitpane_0/JXLayer_0/JXPanel_0/JXLayer_0/TabbedModuleForm_0/TabbedPaneExt_0/Tab_0/FormAdapter_0/JPanel_0/GUI_BE/Betriebename_0/JXPanel_0 ";

		logger.info( "Starting performance test..." );
		final long start = System.currentTimeMillis();
		for ( int index = 0; index < 1000000; index++ ) {
			StringSimilarity.pathSimilarity( path0, path1 );
		}
		logger.info( "Took {} ms.", System.currentTimeMillis() - start );
	}

	@Target( { ElementType.ANNOTATION_TYPE, ElementType.PARAMETER } )
	@Retention( RetentionPolicy.RUNTIME )
	@AlphaChars
	@NumericChars
	@Chars( { '[', ']' } )
	private @interface RandomPath {}

}
