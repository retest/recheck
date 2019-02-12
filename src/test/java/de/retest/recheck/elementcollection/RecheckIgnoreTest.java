package de.retest.recheck.elementcollection;

import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;

public class RecheckIgnoreTest {

	private static final Element SOME = Element.create( "id", mock( Element.class ),
			IdentifyingAttributes.create( Path.fromString(
					"Window[1]/JRootPane[1]/JLayeredPane[1]/TableSelectionDemo[1]/JScrollPane[2]/JTextArea[1]" ),
					"javax.swing.JTextArea" ),
			new Attributes() );

	@Test
	public void ignored_component_should_be_ignored() throws Exception {
		final RecheckIgnore ignored = RecheckIgnore.getTestInstance();
		ignored.ignoreElement( SOME );

		assertThat( ignored.shouldIgnoreElement( SOME ) ).isTrue();
		assertThat( ignored.shouldIgnoreElement( SOME.getIdentifyingAttributes() ) ).isTrue();
	}

	@Test
	public void not_ignored_elements_should_not_be_ignored() throws Exception {
		final RecheckIgnore ignored = RecheckIgnore.getTestInstance();

		assertThat( ignored.shouldIgnoreElement( SOME ) ).isFalse();
		assertThat( ignored.shouldIgnoreElement( SOME.getIdentifyingAttributes() ) ).isFalse();
	}

	@Test
	public void filter_should_remove_ignored_elements() throws Exception {
		final RecheckIgnore ignored = RecheckIgnore.getTestInstance();
		ignored.ignoreElement( SOME );
		final ArrayList<RootElement> windows = new ArrayList<>();
		final RootElement rootElement =
				new RootElement( "id", IdentifyingAttributes.create( fromString( "window[1]" ), window.class ),
						new Attributes(), null, null, 0, "Title" );
		rootElement.addChildren( SOME );
		windows.add( rootElement );
		assertThat( windows.get( 0 ).getContainedElements() ).hasSize( 1 );

		final RootElement filtered = ignored.filter( windows ).get( 0 );
		assertThat( filtered.getContainedElements() ).isEmpty();
	}

	@Test
	public void only_ignored_attribute_should_be_ignored() throws Exception {
		final RecheckIgnore ignored = RecheckIgnore.getTestInstance();
		ignored.ignoreAttribute( SOME, "text" );
		assertThat( ignored.shouldIgnoreAttribute( SOME.getIdentifyingAttributes(), "text" ) ).isTrue();
		assertThat( ignored.shouldIgnoreAttribute( SOME.getIdentifyingAttributes(), "color" ) ).isFalse();
		assertThat( ignored.shouldIgnoreElement( SOME ) ).isFalse();
		ignored.unignoreAttribute( "id", SOME.getIdentifyingAttributes(), "text" );
		assertThat( ignored.shouldIgnoreAttribute( SOME.getIdentifyingAttributes(), "text" ) ).isFalse();
		assertThat( ignored.shouldIgnoreAttribute( SOME.getIdentifyingAttributes(), "color" ) ).isFalse();
		assertThat( ignored.shouldIgnoreElement( SOME ) ).isFalse();
	}

	@Test
	public void different_ignored_identifyingAttributes_should_still_be_ignored() throws Exception {
		final Path path = fromString(
				"Window[1]/JRootPane[1]/JLayeredPane[1]/TableSelectionDemo[1]/JScrollPane[2]/JTextArea[1]" );
		final String type = "javax.swing.JTextArea";
		final Element v1 = Element.create( "id", mock( Element.class ), IdentifyingAttributes.create( path, type ),
				new Attributes() );
		final RecheckIgnore ignored = RecheckIgnore.getTestInstance();
		ignored.ignoreAttribute( v1, "text" );
		final Element v2 = Element.create( "id", mock( Element.class ), IdentifyingAttributes.create( path, type ),
				new Attributes() );
		assertThat( ignored.shouldIgnoreAttribute( v2.getIdentifyingAttributes(), "text" ) ).isTrue();
	}

	@Test
	public void reload_should_overwritte_current_ignored_elements() throws Exception {
		final Element element0 = mock( Element.class );
		final ElementCollection elementCollection0 = new ElementCollection();
		elementCollection0.add( element0 );
		final RecheckIgnore recheckIgnore = RecheckIgnore.getTestInstance( elementCollection0 );

		assertThat( recheckIgnore.getIgnored() ).isEqualTo( elementCollection0 );

		@SuppressWarnings( "unchecked" )
		final Persistence<ElementCollection> persistence = mock( Persistence.class );
		recheckIgnore.reloadRecheckIgnore( persistence );

		assertThat( recheckIgnore.getIgnored() ).isEmpty();
	}

	private static class window {
	}
}
