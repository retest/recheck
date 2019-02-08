package de.retest.recheck.image;

import java.awt.image.BufferedImage;

import de.retest.recheck.ui.image.Screenshot;

public interface ImageDifferenceCalculator {

	ImageDifference compare( BufferedImage img1, BufferedImage img2 );

	ImageDifference compare( Screenshot expected, Screenshot actual );
}
