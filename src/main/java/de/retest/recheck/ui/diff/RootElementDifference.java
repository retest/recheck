package de.retest.recheck.ui.diff;

import java.awt.image.BufferedImage;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.image.ImageDifferenceCalculator;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.ScreenshotAttributeDifference;
import de.retest.recheck.ui.image.ImageUtils;
import de.retest.recheck.ui.image.Screenshot;

public class RootElementDifference implements Difference {

	private static final Logger logger = LoggerFactory.getLogger( RootElementDifference.class );

	private static final long serialVersionUID = 1L;

	private final String differenceId;

	private final String title;

	private final String screen;

	private final int screenId;

	private final int checkedUiComponentCount;

	protected final ElementDifference elementDifference;

	private final Screenshot expectedScreenshot;

	private final Screenshot actualScreenshot;

	@SuppressWarnings( "unused" )
	private RootElementDifference() {
		// for JAXB
		super();
		differenceId = null;
		title = null;
		screen = null;
		screenId = 0;
		elementDifference = null;
		expectedScreenshot = null;
		actualScreenshot = null;
		checkedUiComponentCount = 0;
	}

	public RootElementDifference( final ElementDifference elementDifference, final RootElement expectedDescriptor,
			final RootElement actualDescriptor ) {
		final RootElement instance = expectedDescriptor != null ? expectedDescriptor : actualDescriptor;
		title = instance.getTitle();
		screen = instance.getScreen();
		screenId = instance.getScreenId();
		differenceId = elementDifference.getIdentifier();
		this.elementDifference = elementDifference;
		expectedScreenshot = elementDifference.getExpectedScreenshot();
		actualScreenshot = elementDifference.getActualScreenshot();
		checkedUiComponentCount = instance.countAllContainedElements();
	}

	@Override
	public String toString() {
		String result = title;
		if ( elementDifference.identifyingAttributesDifference != null ) {
			return result + IdentifyingAttributesDifference.class.getSimpleName() + " "
					+ elementDifference.identifyingAttributesDifference;
		}
		if ( elementDifference.attributesDifference != null ) {
			result += AttributesDifference.class.getSimpleName() + " " + elementDifference.attributesDifference;
		}
		if ( !elementDifference.childDifferences.isEmpty() ) {
			result += elementDifference.childDifferences.toString();
		}
		return result;
	}

	@Override
	public int size() {
		return elementDifference.size();
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		return elementDifference.getNonEmptyDifferences();
	}

	public ElementDifference getElementDifference() {
		return elementDifference;
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return elementDifference.getElementDifferences();
	}

	public List<ElementDifference> getElementDifferencesGrouped() {
		//TODO Implement accordingly
		// three or more differences with the same parent element
		// that is not the rootlayer
		// should be grouped as such
		return elementDifference.getElementDifferences();
	}

	public String getIdentifier() {
		return differenceId;
	}

	public Screenshot getExpectedScreenshot() {
		final ScreenshotAttributeDifference screenshotDiff = getScreenshotDifference();
		if ( screenshotDiff != null ) {
			try {
				// Try to instantiate difference image.
				final Class<?> clazz = Class.forName( screenshotDiff.getStrategyName() );
				final ImageDifferenceCalculator imgDiffCalc = (ImageDifferenceCalculator) clazz.newInstance();
				final BufferedImage differenceImage =
						imgDiffCalc.compare( expectedScreenshot, actualScreenshot ).getDifferenceImage();
				return ImageUtils.image2Screenshot( expectedScreenshot.getPersistenceIdPrefix(), differenceImage );
			} catch ( final Exception e ) {
				logger.error( "Exception creating difference image.", e );
			}
		}
		return expectedScreenshot;
	}

	ScreenshotAttributeDifference getScreenshotDifference() {
		final AttributesDifference attributesDifference = elementDifference.getAttributesDifference();
		if ( attributesDifference != null ) {
			final List<AttributeDifference> diffs = attributesDifference.getDifferences();
			for ( final AttributeDifference attrDiff : diffs ) {
				if ( attrDiff instanceof ScreenshotAttributeDifference ) {
					return (ScreenshotAttributeDifference) attrDiff;
				}
			}
		}
		return null;
	}

	public Screenshot getActualScreenshot() {
		return actualScreenshot;
	}

	public int getCheckedUiComponentCount() {
		return checkedUiComponentCount;
	}

	public String getTitle() {
		return title;
	}
}
