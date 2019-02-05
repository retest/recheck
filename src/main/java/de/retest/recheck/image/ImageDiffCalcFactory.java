package de.retest.recheck.image;

public class ImageDiffCalcFactory {

	private ImageDiffCalcFactory() {}

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( ImageDiffCalcFactory.class );

	private static final String IMAGE_DIFFERENCE_CALCULATOR = "de.retest.image.DifferenceCalculator";

	public static ImageDifferenceCalculator getConfiguredImageDifferenceCalculator() {
		final String configured = System.getProperty( IMAGE_DIFFERENCE_CALCULATOR );
		if ( configured != null ) {
			try {
				return (ImageDifferenceCalculator) Class.forName( configured ).newInstance();
			} catch ( final Exception exc ) {
				logger.error( "Error creating configured ImageDifferenceCalculator {}:", configured, exc );
			}
		}
		return new ExactImageDifferenceCalculator();
	}
}
