package de.retest.recheck.image;

import static de.retest.recheck.ui.image.ImageUtils.readImage;
import static de.retest.recheck.ui.image.ImageUtils.scaleProportionallyToMaxWidthHeight;
import static de.retest.recheck.ui.image.ImageUtils.scaleToSameSize;
import static de.retest.recheck.ui.image.ImageUtils.screenshot2Image;
import static de.retest.recheck.ui.image.ImageUtils.toBufferedImage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

import javax.swing.GrayFilter;

import de.retest.recheck.ui.image.Screenshot;

// Inspired by http://mindmeat.blogspot.de/2008/07/java-image-comparison.html

// Alternative is using full-fledged (but native / OS-dependent):
// http://docs.opencv.org/2.4/doc/tutorials/tutorials.html
public class FuzzyImageDifferenceCalculator implements ImageDifferenceCalculator {

	private static final org.slf4j.Logger logger =
			org.slf4j.LoggerFactory.getLogger( FuzzyImageDifferenceCalculator.class );

	private static final int BLOCKSIZE = 10;
	private static final int SENSITIVITY = 3;
	private static final double STABILIZER = 25.0;

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
				return new ImageDifference( 1.0, null, FuzzyImageDifferenceCalculator.class );
			}
			return new ImageDifference( 0.0, img2, FuzzyImageDifferenceCalculator.class );
		}
		if ( img2 == null ) {
			return new ImageDifference( 0.0, img1, FuzzyImageDifferenceCalculator.class );
		}
		img1 = toBufferedImage( scaleProportionallyToMaxWidthHeight( img1, 800, 600 ) );
		img1 = toBufferedImage( scaleToSameSize( img1, img2 ) );
		img2 = toBufferedImage( scaleToSameSize( img2, img1 ) );
		final BufferedImage differenceImage = toBufferedImage( img2 );
		final Graphics2D gc = differenceImage.createGraphics();
		gc.setColor( Color.RED );
		// convert to gray images.
		img1 = toBufferedImage( GrayFilter.createDisabledImage( img1 ) );
		img2 = toBufferedImage( GrayFilter.createDisabledImage( img2 ) );
		// set to a match by default, if a change is found then flag non-match
		int numdiffs = 0;
		final int numRows = (int) Math.ceil( img1.getHeight() / (float) BLOCKSIZE );
		final int numCols = (int) Math.ceil( img1.getWidth() / (float) BLOCKSIZE );
		// loop through whole image and compare individual blocks of images
		StringBuilder textual = new StringBuilder();
		for ( int row = 0; row < numRows; row++ ) {
			textual.append( "|" );
			for ( int col = 0; col < numCols; col++ ) {
				final int b1 = getAverageBrightness( getSubImage( img1, col, row ) );
				final int b2 = getAverageBrightness( getSubImage( img2, col, row ) );
				final int diff = Math.abs( b1 - b2 );
				if ( diff > SENSITIVITY ) {
					// the difference in a certain region has passed the threshold value
					// draw an indicator on the change image to show where change was detected.
					// TODO Merge borders of neighboring blocks
					gc.drawRect( col * BLOCKSIZE, row * BLOCKSIZE, BLOCKSIZE - 1, BLOCKSIZE - 1 );
					numdiffs++;
				}
				textual.append( diff > SENSITIVITY ? "X" : " " );
			}
			textual.append( "|" );
			logger.warn( textual.toString() );
			textual = new StringBuilder();
		}
		if ( numdiffs == 0 ) {
			// ensure no rounding errors...
			return new ImageDifference( 1.0, differenceImage, FuzzyImageDifferenceCalculator.class );
		}
		final int total = numRows * numCols;
		final double match = (total - numdiffs) / (double) total;
		return new ImageDifference( match, differenceImage, FuzzyImageDifferenceCalculator.class );
	}

	// returns a value specifying some kind of average brightness in the image.
	protected int getAverageBrightness( final BufferedImage img ) {
		final Raster r = img.getData();
		int total = 0;
		for ( int y = 0; y < r.getHeight(); y++ ) {
			for ( int x = 0; x < r.getWidth(); x++ ) {
				total += r.getSample( r.getMinX() + x, r.getMinY() + y, 0 );
			}
		}
		return (int) (total / (r.getWidth() / STABILIZER * (r.getHeight() / STABILIZER)));
	}

	private BufferedImage getSubImage( final BufferedImage img, final int col, final int row ) {
		final int x = col * BLOCKSIZE;
		final int width = Math.abs( Math.min( img.getWidth() - x, BLOCKSIZE - 1 ) );
		final int y = row * BLOCKSIZE;
		final int height = Math.abs( Math.min( img.getHeight() - y, BLOCKSIZE - 1 ) );
		return img.getSubimage( x, y, width, height );
	}
}
