package de.retest.ui.actions;

import java.awt.event.InputEvent;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

// TODO Any combination possible!
@XmlAccessorType( XmlAccessType.FIELD )
public final class KeyModifier implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final KeyModifier CONTROL = new KeyModifier( InputEvent.CTRL_DOWN_MASK );
	public static final KeyModifier SHIFT = new KeyModifier( InputEvent.SHIFT_DOWN_MASK );
	public static final KeyModifier ALT = new KeyModifier( InputEvent.ALT_DOWN_MASK );
	public static final KeyModifier ALT_GR = new KeyModifier( InputEvent.ALT_GRAPH_DOWN_MASK );
	public static final KeyModifier META = new KeyModifier( InputEvent.META_DOWN_MASK );
	public static final KeyModifier NONE = new KeyModifier( 0 );

	@XmlElement
	private final int code;

	@SuppressWarnings( "unused" )
	private KeyModifier() {
		// Only used by JAXB
		code = -1;
	}

	public KeyModifier( final int code ) {
		int modifiers = code;
		if ( (modifiers & InputEvent.BUTTON1_MASK) != 0 ) {
			modifiers ^= InputEvent.BUTTON1_MASK;
		}
		if ( (modifiers & InputEvent.BUTTON2_MASK) != 0 ) {
			modifiers ^= InputEvent.BUTTON2_MASK;
		}
		if ( (modifiers & InputEvent.BUTTON3_MASK) != 0 ) {
			modifiers ^= InputEvent.BUTTON3_MASK;
		}
		if ( (modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0 ) {
			modifiers ^= InputEvent.BUTTON1_DOWN_MASK;
		}
		if ( (modifiers & InputEvent.BUTTON2_DOWN_MASK) != 0 ) {
			modifiers ^= InputEvent.BUTTON2_DOWN_MASK;
		}
		if ( (modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0 ) {
			modifiers ^= InputEvent.BUTTON3_DOWN_MASK;
		}
		this.code = modifiers;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		if ( code == 0 ) {
			return "";
		}
		final StringBuilder stringBuilder = new StringBuilder();
		if ( (code & InputEvent.META_DOWN_MASK) != 0 ) {
			stringBuilder.append( "Meta" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.CTRL_DOWN_MASK) != 0 ) {
			stringBuilder.append( "Ctrl" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.ALT_DOWN_MASK) != 0 ) {
			stringBuilder.append( "Alt" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.SHIFT_DOWN_MASK) != 0 ) {
			stringBuilder.append( "Shift" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.ALT_GRAPH_DOWN_MASK) != 0 ) {
			stringBuilder.append( "Alt Graph" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.META_MASK) != 0 ) {
			stringBuilder.append( "Meta" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.CTRL_MASK) != 0 ) {
			stringBuilder.append( "Ctrl" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.ALT_MASK) != 0 ) {
			stringBuilder.append( "Alt" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.SHIFT_MASK) != 0 ) {
			stringBuilder.append( "Shift" );
			stringBuilder.append( "+" );
		}
		if ( (code & InputEvent.ALT_GRAPH_MASK) != 0 ) {
			stringBuilder.append( "Alt Graph" );
			stringBuilder.append( "+" );
		}
		if ( stringBuilder.length() > 0 ) {
			stringBuilder.setLength( stringBuilder.length() - 1 );
		}
		return stringBuilder.toString();
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		final KeyModifier that = (KeyModifier) o;

		return code == that.code;
	}

	@Override
	public int hashCode() {
		return code;
	}
}
