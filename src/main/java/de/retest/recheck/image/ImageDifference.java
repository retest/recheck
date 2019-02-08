package de.retest.recheck.image;

import java.awt.image.BufferedImage;

public class ImageDifference {
	// to make sure to account for rounding errors
	private static final double EQUALITY_THRESHOLD = 0.99;

	private final BufferedImage differenceImage;
	private final double match;
	private final String strategyName;

	public ImageDifference( final double match, final BufferedImage differenceImage, final Class<?> strategy ) {
		this.match = match;
		this.differenceImage = differenceImage;
		strategyName = strategy.getName();
	}

	public BufferedImage getDifferenceImage() {
		return differenceImage;
	}

	public double getMatch() {
		return match;
	}

	public boolean isEqual() {
		return match > EQUALITY_THRESHOLD;
	}

	public String getStrategyName() {
		return strategyName;
	}
}
