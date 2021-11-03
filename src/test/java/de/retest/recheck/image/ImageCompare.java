package de.retest.recheck.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageCompare {

	public static ImageCompare assertThatImage( final BufferedImage actual ) {
		return new ImageCompare( actual );
	}

	private final BufferedImage actual;

	public ImageCompare( final BufferedImage actual ) {
		this.actual = actual;
	}

	public void isEqualTo( final File expectedFile ) {
		BufferedImage expected = null;
		try {
			expected = ImageIO.read( expectedFile );
		} catch ( final Exception exc ) {
			throw new RuntimeException( exc );
		}
		if ( actual.getHeight() != expected.getHeight() ) {
			throw new AssertionError(
					"Images have different height: " + actual.getHeight() + " vs " + expected.getHeight() );
		}
		if ( actual.getWidth() != expected.getWidth() ) {
			throw new AssertionError(
					"Images have different width: " + actual.getWidth() + " vs " + expected.getWidth() );
		}
		for ( int x = 0; x < actual.getWidth(); x++ ) {
			for ( int y = 0; y < actual.getHeight(); y++ ) {
				if ( !closeEnough( actual.getRGB( x, y ), expected.getRGB( x, y ) ) ) {
					throw new AssertionError( "Images differ at " + x + "/" + y + ": "
							+ new Color( actual.getRGB( x, y ) ) + " vs " + new Color( expected.getRGB( x, y ) ) );
				}
			}
		}
	}

	private boolean closeEnough( final int rgbActual, final int rgbExpected ) {
		final Color actual = new Color( rgbActual );
		final Color expected = new Color( rgbExpected );
		if ( Math.abs( actual.getBlue() - expected.getBlue() ) > 1
				|| Math.abs( actual.getGreen() - expected.getGreen() ) > 1
				|| Math.abs( actual.getRed() - expected.getRed() ) > 1 ) {
			return false;
		}
		return true;
	}
}
