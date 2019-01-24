package de.retest.ui.actions;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum MouseClickMode {
	Click( MouseEvent.BUTTON1, 1 ),
	RightClick( MouseEvent.BUTTON3, 1 ),
	DoubleClick( MouseEvent.BUTTON1, 2 );

	private static final Logger logger = LoggerFactory.getLogger( MouseClickMode.class );

	private final int button;
	private final int clickCount;

	private MouseClickMode( final int button, final int clickCount ) {
		this.button = button;
		this.clickCount = clickCount;
	}

	public int getButton() {
		return button;
	}

	public int getClickCount() {
		return clickCount;
	}

	public static MouseClickMode getModeFromEvent( final MouseEvent mouseEvent ) {
		if ( mouseEvent.getClickCount() == MouseClickMode.DoubleClick.getClickCount() ) {
			return MouseClickMode.DoubleClick;
		}
		if ( SwingUtilities.isLeftMouseButton( mouseEvent ) ) {
			return MouseClickMode.Click;
		}
		if ( SwingUtilities.isRightMouseButton( mouseEvent ) ) {
			return MouseClickMode.RightClick;
		}
		if ( SwingUtilities.isMiddleMouseButton( mouseEvent ) ) {
			logger.error( "MiddleMouseButton not implemented!" );
		}
		throw new IllegalStateException( "Unreachable Code: " + mouseEvent.getButton() );
	}
}
