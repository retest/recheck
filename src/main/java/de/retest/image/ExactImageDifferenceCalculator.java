package de.retest.image;

import static de.retest.ui.image.ImageUtils.readImage;
import static de.retest.ui.image.ImageUtils.scaleToSameSize;
import static de.retest.ui.image.ImageUtils.screenshot2Image;
import static de.retest.ui.image.ImageUtils.toBufferedImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import de.retest.ui.image.Screenshot;

public class ExactImageDifferenceCalculator implements ImageDifferenceCalculator {

	public ImageDifference compare( final String file1, final String file2 ) throws IOException {
		return compare( readImage( file1 ), readImage( file2 ) );
	}

	@Override
	public ImageDifference compare( final Screenshot shot1, final Screenshot shot2 ) {
		return compare( screenshot2Image( shot1 ), screenshot2Image( shot2 ) );
	}

	@Override
	public ImageDifference compare( BufferedImage img1, BufferedImage img2 ) {
		if ( img1 == null ) {
			if ( img2 == null ) {
				return new ImageDifference( 1.0, null, ExactImageDifferenceCalculator.class );
			}
			return new ImageDifference( 0.0, img2, ExactImageDifferenceCalculator.class );
		}
		if ( img2 == null ) {
			return new ImageDifference( 0.0, img1, ExactImageDifferenceCalculator.class );
		}
		img1 = toBufferedImage( scaleToSameSize( img1, img2 ) );
		img2 = toBufferedImage( scaleToSameSize( img2, img1 ) );
		// convert images to pixel arrays...
		final int w = img1.getWidth();
		final int h = img1.getHeight();
		final int highlight = Color.MAGENTA.getRGB();
		final int[] p1 = img1.getRGB( 0, 0, w, h, null, 0, w );
		final int[] p2 = img2.getRGB( 0, 0, w, h, null, 0, w );
		// compare img1 to img2, pixel by pixel. If different, highlight img1's pixel...
		int diffTotal = 0;
		for ( int i = 0; i < p1.length; i++ ) {
			if ( p1[i] != p2[i] ) {
				p1[i] = highlight;
				diffTotal++;
			}
		}
		// save img1's pixels to a new BufferedImage, and return it...
		// (May require TYPE_INT_ARGB)
		final BufferedImage out = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
		out.setRGB( 0, 0, w, h, p1, 0, w );
		return new ImageDifference( (p1.length - diffTotal) / (double) p1.length, out,
				ExactImageDifferenceCalculator.class );
	}

}
