package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RenderContainedElementsAdapter;

public class RenderContainedElementsAdapterTest {

	@Test
	public void element_should_exist_exactly_once_when_unmarshalling() throws Exception {
		final RenderContainedElementsAdapter cut = new RenderContainedElementsAdapter();
		final Element e0 = cut.unmarshal( createNewElement() );
		final Element e1 = cut.unmarshal( createNewElement() );

		assertThat( cut.unmarshal( createNewElement() ) ).isSameAs( e0 );
		assertThat( cut.unmarshal( createNewElement() ) ).isSameAs( e1 );
	}

	private Element createNewElement() {
		return Element.create(
				"id", mock( Element.class ), IdentifyingAttributes
						.create( Path.fromString( "Window[0]/path[0]/Component[0]" ), java.awt.Component.class ),
				new Attributes() );
	}

}
