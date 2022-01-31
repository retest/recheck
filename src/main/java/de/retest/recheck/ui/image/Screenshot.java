package de.retest.recheck.ui.image;

import java.io.Serializable;

import de.retest.recheck.util.ChecksumCalculator;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * We use this class that simply wraps a byte array instead of the Java internal format
 * {@link java.awt.image.BufferedImage}, because a screenshot is a small PNG of a few KB size, but a huuuuge
 * BufferedImage that very quickly fills up our working memory...
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Screenshot implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ImageType {
		PNG;

		public String getFileExtension() {
			return name().toLowerCase();
		}
	}

	@XmlElement
	private final String persistenceId;

	@XmlTransient
	private byte[] binaryData;

	@XmlTransient
	private transient String sha256;

	@XmlElement
	private final ImageType type;

	@SuppressWarnings( "unused" )
	private Screenshot() {
		// required by JAXB
		persistenceId = null;
		binaryData = null;
		type = null;
	}

	public Screenshot( final String persistenceId, final byte[] binaryData, final ImageType type ) {
		if ( binaryData == null ) {
			throw new NullPointerException( "binaryData must not be null." );
		}
		if ( type == null ) {
			throw new NullPointerException( "type must not be null." );
		}
		this.binaryData = binaryData;
		this.type = type;
		this.persistenceId = persistenceId;
		ensureSha();
	}

	public byte[] getBinaryData() {
		return binaryData;
	}

	public void setBinaryData( final byte[] binaryData ) {
		this.binaryData = binaryData;
		sha256 = null;
		ensureSha();
	}

	public ImageType getType() {
		return type;
	}

	public String getPersistenceId() {
		return persistenceId;
	}

	@Override
	public int hashCode() {
		ensureSha();
		return type.hashCode() + (persistenceId.hashCode() * 31 + sha256.hashCode()) * 31;
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null || !(obj instanceof Screenshot) ) {
			return false;
		}
		final Screenshot other = (Screenshot) obj;
		if ( type != other.type || !persistenceId.equals( other.persistenceId ) ) {
			return false;
		}

		// most expensive, should be last
		ensureSha();
		other.ensureSha();
		return sha256.equals( other.sha256 );
	}

	private void ensureSha() {
		if ( binaryData != null ) {
			sha256 = ChecksumCalculator.getInstance().sha256( binaryData );
		} else {
			sha256 = "";
		}
	}

	@Override
	public String toString() {
		return "Screenshot of " + persistenceId;
	}
}
