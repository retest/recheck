package de.retest.util;

import static de.retest.util.ChecksumCalculator.getInstance;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ChecksumCalculatorTest {

	private static final String TEST_1 = "testdata";
	private static final String TEST_MD5_1 = "ef654c40ab4f1747fc699915d4f70902";
	private static final String TEST_SHA256_1 = "810ff2fb242a5dee4220f2cb0e6a519891fb67f2f828a6cab4ef8894633b1f50";

	private static final String TEST_2 = "testdata2";
	private static final String TEST_MD5_2 = "0e65de7114f9d086a6176fdda0f86e9f";
	private static final String TEST_SHA256_2 = "80905dfa0896b4f02b65e7fc3f7244e2f20bf7346191db14fa66f0645790244d";

	private static final String TEST_3 = "testdata7";
	private static final String TEST_MD5_3 = "f19ae056a94044dbb7ef16981a3be665";
	private static final String TEST_SHA256_3 = "01700968b1586c4310b35b5b41d64261122bfcc8179a33863e1f748765c61b23";

	@SuppressWarnings( "deprecation" )
	@Test
	public void regresion_test_for_MD5_calculation() {
		assertThat( getInstance().md5( TEST_1 ) ).isEqualTo( TEST_MD5_1 );
		assertThat( getInstance().md5( TEST_2 ) ).isEqualTo( TEST_MD5_2 );
		assertThat( getInstance().md5( TEST_3 ) ).isEqualTo( TEST_MD5_3 );

		assertThat( getInstance().md5( TEST_1.getBytes() ) ).isEqualTo( TEST_MD5_1 );
		assertThat( getInstance().md5( TEST_2.getBytes() ) ).isEqualTo( TEST_MD5_2 );
		assertThat( getInstance().md5( TEST_3.getBytes() ) ).isEqualTo( TEST_MD5_3 );
	}

	@Test
	public void regresion_test_for_SHA256_calculation() {
		assertThat( getInstance().sha256( TEST_1 ) ).isEqualTo( TEST_SHA256_1 );
		assertThat( getInstance().sha256( TEST_2 ) ).isEqualTo( TEST_SHA256_2 );
		assertThat( getInstance().sha256( TEST_3 ) ).isEqualTo( TEST_SHA256_3 );

		assertThat( getInstance().sha256( TEST_1.getBytes() ) ).isEqualTo( TEST_SHA256_1 );
		assertThat( getInstance().sha256( TEST_2.getBytes() ) ).isEqualTo( TEST_SHA256_2 );
		assertThat( getInstance().sha256( TEST_3.getBytes() ) ).isEqualTo( TEST_SHA256_3 );
	}

}
