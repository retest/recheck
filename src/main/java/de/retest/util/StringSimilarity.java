package de.retest.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.FuzzyScore;

public class StringSimilarity {

	private StringSimilarity() {}

	private static final FuzzyScore fuzzyScore = new FuzzyScore( Locale.GERMAN );

	public static double textSimilarity( final String text0, final String text1 ) {
		if ( text0 == null || text1 == null ) {
			return simpleSimilarity( text0, text1 );
		}

		if ( text0.equals( text1 ) ) {
			return 1.0;
		}

		final double fuzzyDistance = fuzzyScore.fuzzyScore( text0, text1 );

		if ( fuzzyDistance == 0.0 ) {
			return 0.0;
		}

		final double maxLength = Math.max( text0.length(), text1.length() );
		final double similarity = fuzzyDistance / (maxLength * 4.0);

		assert similarity >= 0.0 && similarity <= 1.0 : "text0 is: '" + text0 + "' - text1 is: '" + text1
				+ "', result is:" + similarity;

		return similarity;
	}

	public static double pathSimilarity( final String path0, final String path1 ) {
		if ( path0 == null || path1 == null ) {
			return simpleSimilarity( path0, path1 );
		}

		if ( path0.equals( path1 ) ) {
			return 1.0;
		}

		if ( path0.isEmpty() || path1.isEmpty() ) {
			return 0.0;
		}

		final String cleanPath0 = removeBrackets( path0 );
		final String cleanPath1 = removeBrackets( path1 );

		final int commonPrefixLength = StringUtils.getCommonPrefix( cleanPath0, cleanPath1 ).length();
		final int commonSuffixLength = getCommonSuffixStartingAt( cleanPath0, cleanPath1, commonPrefixLength );
		final int minLength = Math.min( cleanPath0.length(), cleanPath1.length() );
		final int maxLength = Math.max( cleanPath0.length(), cleanPath1.length() );

		final int difference =
				Math.abs( minLength - (commonPrefixLength + commonSuffixLength) ) + maxLength - minLength;
		double similarity = (maxLength - difference) / (double) maxLength;
		similarity = similarity * similarity;

		assert similarity >= 0.0 && similarity <= 1.0 : "path0 is: '" + path0 + "' - path1 is: '" + path1
				+ "', result is:" + similarity;

		return similarity;
	}

	private static int getCommonSuffixStartingAt( final String path0, final String path1, final int start ) {
		int commonSuffixLength = 0;
		for ( int idxP0 = path0.length() - 1, idxP1 = path1.length() - 1; idxP0 >= start
				&& idxP1 >= start; idxP0--, idxP1-- ) {
			final char c0 = path0.charAt( idxP0 );
			final char c1 = path1.charAt( idxP1 );
			if ( c0 == c1 ) {
				commonSuffixLength++;
			} else {
				return commonSuffixLength;
			}
		}
		return commonSuffixLength;
	}

	public static double simpleSimilarity( final String s0, final String s1 ) {
		if ( StringUtils.equals( s0, s1 ) ) {
			return 1.0;
		}
		return 0.0;
	}

	private static String removeBrackets( final String path ) {
		return path.replaceAll( "[\\[\\]]", "" );
	}

}
