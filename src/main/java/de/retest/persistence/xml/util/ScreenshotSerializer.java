package de.retest.persistence.xml.util;

import java.io.File;

import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.image.ImageUtils;
import de.retest.ui.image.Screenshot;
import de.retest.util.FileUtil;

public class ScreenshotSerializer {

	public static final String EXPECTED_POSTFIX = "_expected";
	public static final String ACTUAL_POSTFIX = "_actual";

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( ScreenshotSerializer.class );

	private File baseDir;

	public ScreenshotSerializer( final File baseDir ) {
		this.baseDir = baseDir;
	}

	public class DiffScreenshots {
		public final File expected;
		public final File actual;

		public DiffScreenshots( final File expected, final File actual ) {
			this.expected = expected;
			this.actual = actual;
		}

		public File getExpected() {
			return expected;
		}

		public File getActual() {
			return actual;
		}
	}

	public DiffScreenshots saveDiffScreenshots( final IdentifyingAttributes identifyingAttributes,
			final Screenshot expectedScreenshot, final Screenshot actualScreenshot ) {
		File expected = null;
		File actual = null;
		if ( expectedScreenshot != null ) {
			expected =
					saveScreenshot( expectedScreenshot, identifyingAttributes.getPath() + EXPECTED_POSTFIX + ".png" );
		}
		if ( actualScreenshot != null ) {
			actual = saveScreenshot( actualScreenshot, identifyingAttributes.getPath() + ACTUAL_POSTFIX + ".png" );
		}
		return new DiffScreenshots( expected, actual );
	}

	public void saveScreenshots( final Element descriptor ) {
		saveScreenshot( descriptor.getScreenshot(), descriptor.getIdentifyingAttributes().getPath() + ".png" );
		for ( final Element childElement : descriptor.getContainedElements() ) {
			saveScreenshots( childElement );
		}
	}

	public File saveScreenshot( final Element component ) {
		if ( component.getScreenshot() == null ) {
			return null;
		}
		return saveScreenshot( component.getScreenshot(), component.getIdentifyingAttributes().getPath() + ".png" );
	}

	public File saveScreenshot( final Screenshot image, final String path ) {
		if ( image == null ) {
			return null;
		}
		try {
			final File result = new File( baseDir, path );
			FileUtil.ensureFolder( result );
			ImageUtils.exportScreenshot( image, result );
			return result;
		} catch ( final Exception exc ) {
			logger.error( "Exception saving screenshot: ", exc );
			return null;
		}
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setBaseDir( final File baseDir ) {
		this.baseDir = baseDir;
	}
}
