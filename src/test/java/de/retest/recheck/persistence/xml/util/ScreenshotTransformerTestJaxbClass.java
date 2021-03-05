package de.retest.recheck.persistence.xml.util;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.ui.image.Screenshot;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ScreenshotTransformerTestJaxbClass extends Persistable {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 2;

	@XmlElement
	private Screenshot screenshot;

	public ScreenshotTransformerTestJaxbClass() {
		super( PERSISTENCE_VERSION );
	}

	public Screenshot getScreenshot() {
		return screenshot;
	}

	public void setScreenshot( final Screenshot screenshot ) {
		this.screenshot = screenshot;
	}
}
