package de.retest.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumCalculator {

	public static final int LENGTH_OF_SHA256 = 64;

	private static ChecksumCalculator instance;

	public static ChecksumCalculator getInstance() {
		if ( instance == null ) {
			instance = new ChecksumCalculator();
		}
		return instance;
	}

	public String sha256( final String input ) {
		return getDigest( "SHA-256", input.getBytes( StandardCharsets.UTF_8 ) );
	}

	public String sha256( final byte[] input ) {
		return getDigest( "SHA-256", input );
	}

	/**
	 * @param input
	 *            string to hash
	 * @return hashed input string
	 * @deprecated use {@link ChecksumCalculator#sha256(String)}
	 */
	@Deprecated
	public String md5( final String input ) {
		return getDigest( "MD5", input.getBytes( StandardCharsets.UTF_8 ) );
	}

	/**
	 * @param input
	 *            bytes to hash
	 * @return hashed input bytes
	 * @deprecated use {@link ChecksumCalculator#sha256(byte[])}
	 */
	@Deprecated
	public String md5( final byte[] input ) {
		return getDigest( "MD5", input );
	}

	private String getDigest( final String algorithm, final byte[] input ) {
		try {
			final MessageDigest digest = MessageDigest.getInstance( algorithm );
			final byte[] hash = digest.digest( input );
			return bytesToHex( hash );
		} catch ( final NoSuchAlgorithmException e ) {
			throw new RuntimeException( e );
		}
	}

	private String bytesToHex( final byte[] bytes ) {
		final StringBuilder result = new StringBuilder();
		for ( final byte b : bytes ) {
			result.append( Integer.toString( (b & 0xff) + 0x100, 16 ).substring( 1 ) );
		}
		return result.toString();
	}

}
