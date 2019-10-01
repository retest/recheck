package de.retest.recheck.ui.review;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.image.Screenshot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class ScreenshotChanges {

	private final Map<IdentifyingAttributes, Screenshot> screenshotMap;

	public static ScreenshotChanges empty() {
		return new ScreenshotChanges( Collections.emptyMap() );
	}

	public static ScreenshotChanges actual( final ActionReplayResult result ) {
		return new ScreenshotChanges( result.getStateDifference().getRootElementDifferences().stream() //
				.map( RootElementDifference::getElementDifference ) //
				.collect( Collectors.toMap( ElementDifference::getIdentifyingAttributes,
						ElementDifference::getActualScreenshot ) ) );
	}

	public Screenshot getScreenshot( final IdentifyingAttributes attribute ) {
		return screenshotMap.get( attribute );
	}
}
