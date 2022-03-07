package de.retest.recheck.ui.descriptors;

import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Rectangle;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.review.ActionChangeSet;

class ElementIT {

	@Test
	void applyChanges_should_remove_slightly_different_element() {
		final Element form = new RootElement( "old", new IdentifyingAttributes( Arrays.asList( //
				new PathAttribute( fromString( "html/body/form" ) ), //
				new SuffixAttribute( 1 ), //
				new TextAttribute( "type", "form" ) //
		) ), new Attributes(), null, "", 0, "" );

		final IdentifyingAttributes original = new IdentifyingAttributes( Arrays.asList( // 
				new PathAttribute( fromString( "html/body/form/input" ) ), //
				new SuffixAttribute( 1 ), //
				new TextAttribute( "type", "input" ), //
				OutlineAttribute.create( new Rectangle( 0, 0, 200, 100 ) ), //
				OutlineAttribute.createAbsolute( new Rectangle( 100, 50, 200, 100 ) ) //
		) );
		final IdentifyingAttributes altered = new IdentifyingAttributes( Arrays.asList( // 
				new PathAttribute( fromString( "html/body/form/input" ) ), //
				new SuffixAttribute( 1 ), //
				new TextAttribute( "type", "input" ), //
				OutlineAttribute.create( new Rectangle( 0, 0, 250, 150 ) ), //
				OutlineAttribute.createAbsolute( new Rectangle( 100, 20, 250, 150 ) ) //
		) );

		form.addChildren( new Element( "input", form, original, new Attributes(), null ) );

		final ActionChangeSet changeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		changeSet.addDeletedChange( altered );

		final Element changed = form.applyChanges( changeSet );
		assertThat( changed.getContainedElements() ).isEmpty();
	}

	@Test
	void applyChanges_should_properly_handle_inserted_deleted_element() {
		final Element form = new RootElement( "old", new IdentifyingAttributes( Arrays.asList( //
				new PathAttribute( fromString( "html/body/form" ) ), //
				new SuffixAttribute( 1 ), //
				new TextAttribute( "type", "form" ) //
		) ), new Attributes(), null, "", 0, "" );

		final IdentifyingAttributes original = new IdentifyingAttributes( Arrays.asList( // 
				new PathAttribute( fromString( "html/body/form/input" ) ), //
				new SuffixAttribute( 1 ), //
				new TextAttribute( "type", "input" ), //
				OutlineAttribute.create( new Rectangle( 0, 0, 200, 100 ) ), //
				OutlineAttribute.createAbsolute( new Rectangle( 100, 50, 200, 100 ) ) //
		) );
		final IdentifyingAttributes altered = new IdentifyingAttributes( Arrays.asList( // 
				new PathAttribute( fromString( "html/body/form/input" ) ), //
				new SuffixAttribute( 1 ), //
				new TextAttribute( "type", "input" ), //
				OutlineAttribute.create( new Rectangle( 0, 0, 250, 150 ) ), //
				OutlineAttribute.createAbsolute( new Rectangle( 100, 20, 250, 150 ) ) //
		) );

		form.addChildren( new Element( "input", form, original, new Attributes(), null ) );

		final ActionChangeSet changeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		changeSet.addDeletedChange( original );
		changeSet.addInsertChange( new Element( "input", form, altered, new Attributes(), null ) );

		final Element changed = form.applyChanges( changeSet );

		assertThat( changed.getContainedElements() ) //
				.hasSize( 1 ) //
				.element( 0 ) //
				.satisfies( input -> {
					assertThat( input.getIdentifyingAttributes() ).isEqualTo( altered );
				} );
	}
}
