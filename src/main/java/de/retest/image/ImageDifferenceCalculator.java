package de.retest.image;

import java.awt.image.BufferedImage;

import de.retest.ui.image.Screenshot;

public interface ImageDifferenceCalculator {

	ImageDifference compare( BufferedImage img1, BufferedImage img2 );

	ImageDifference compare( Screenshot expected, Screenshot actual );
}
