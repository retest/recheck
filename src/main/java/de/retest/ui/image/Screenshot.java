package de.retest.ui.image;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.retest.util.ChecksumCalculator;

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

	/**
	 * This should be final, too. When the corresponding setter is invoked, the persistence ID is <em>not</em> updated.
	 * However, as we take care of this in our surrounding implementation, this shouldn't be an issue. Nonetheless, one
	 * should note that this class is broken since it can be in a corrupt state.
	 */
	@XmlTransient
	private byte[] binaryData;

	@XmlElement
	private final ImageType type;

	@SuppressWarnings( "unused" )
	private Screenshot() {
		// required by JAXB
		persistenceId = null;
		binaryData = null;
		type = null;
	}

	public Screenshot( final String prefix, final byte[] binaryData, final ImageType type ) {
		if ( binaryData == null ) {
			throw new NullPointerException( "binaryData must not be null." );
		}
		if ( type == null ) {
			throw new NullPointerException( "type must not be null." );
		}
		this.binaryData = binaryData;
		this.type = type;
		persistenceId = createPersistenceId( prefix, binaryData );
	}

	public byte[] getBinaryData() {
		return binaryData;
	}

	public void setBinaryData( final byte[] binaryData ) {
		this.binaryData = binaryData;
	}

	public ImageType getType() {
		return type;
	}

	public String getPersistenceId() {
		return persistenceId;
	}

	private static final String PERSISTENCE_ID_SEPARATOR = "_";

	private static final String REGEX_PREFIX_EXTRACT =
			PERSISTENCE_ID_SEPARATOR + "[0-9a-f]{" + ChecksumCalculator.LENGTH_OF_SHA256 + "}$";

	public static String createPersistenceId( final String prefix, final byte[] binaryData ) {
		if ( prefix.length() > ChecksumCalculator.LENGTH_OF_SHA256 ) {
			throw new RuntimeException( "prefix (" + prefix + ") looks like a full persistenceId" );
		}
		return prefix + PERSISTENCE_ID_SEPARATOR + ChecksumCalculator.getInstance().sha256( binaryData );
	}

	public static String getPersistenceIdPrefix( final String persistenceId ) {
		return persistenceId.replaceAll( REGEX_PREFIX_EXTRACT, "" );
	}

	public String getPersistenceIdPrefix() {
		return getPersistenceIdPrefix( persistenceId );
	}

	@Override
	public int hashCode() {
		return type.hashCode() * 31 + persistenceId.hashCode();
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof Screenshot) ) {
			return false;
		}
		final Screenshot other = (Screenshot) obj;
		if ( type != other.type ) {
			return false;
		}

		return persistenceId.equals( other.persistenceId );
	}

	@Override
	public String toString() {
		return "Screenshot of " + getPersistenceIdPrefix();
	}
}
