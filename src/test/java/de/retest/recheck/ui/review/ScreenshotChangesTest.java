package de.retest.recheck.ui.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.ui.image.Screenshot;

class ScreenshotChangesTest {

	ActionReplayResult actionReplayResult;

	Screenshot screenshot;

	IdentifyingAttributes identifyingForPresentScreenshot;
	IdentifyingAttributes identifyingForMissingScreenshot;

	@BeforeEach
	void setUp() {
		screenshot = mock( Screenshot.class );
		identifyingForPresentScreenshot = mock( IdentifyingAttributes.class );
		identifyingForMissingScreenshot = mock( IdentifyingAttributes.class );

		final ElementDifference elementWithScreenshot = mock( ElementDifference.class );
		when( elementWithScreenshot.getIdentifyingAttributes() ).thenReturn( identifyingForPresentScreenshot );
		when( elementWithScreenshot.getActualScreenshot() ).thenReturn( screenshot );

		final RootElementDifference rootElementWithScreenshot = mock( RootElementDifference.class );
		when( rootElementWithScreenshot.getElementDifference() ).thenReturn( elementWithScreenshot );
		when( rootElementWithScreenshot.getActualScreenshot() ).thenReturn( screenshot );

		final ElementDifference elementWithoutScreenshot = mock( ElementDifference.class );
		when( elementWithoutScreenshot.getIdentifyingAttributes() ).thenReturn( identifyingForMissingScreenshot );

		final RootElementDifference rootElementWithoutScreenshot = mock( RootElementDifference.class );
		when( rootElementWithoutScreenshot.getElementDifference() ).thenReturn( elementWithoutScreenshot );

		final StateDifference stateDifference = mock( StateDifference.class );
		when( stateDifference.getRootElementDifferences() )
				.thenReturn( Arrays.asList( rootElementWithScreenshot, rootElementWithoutScreenshot ) );

		actionReplayResult = mock( ActionReplayResult.class );
		when( actionReplayResult.getStateDifference() ).thenReturn( stateDifference );
	}

	@Test
	void actual_should_properly_lookup_element_screenshots() throws Exception {
		final ScreenshotChanges cut = ScreenshotChanges.actual( actionReplayResult );

		assertThat( cut.getScreenshot( identifyingForPresentScreenshot ) ).isEqualTo( screenshot );
		assertThat( cut.getScreenshot( identifyingForMissingScreenshot ) ).isNull();
	}

	@Test
	void actual_should_not_produce_null_pointer_with_null_screenshots() throws Exception {
		assertThatCode( () -> ScreenshotChanges.actual( actionReplayResult ) ).doesNotThrowAnyException();
	}
}
