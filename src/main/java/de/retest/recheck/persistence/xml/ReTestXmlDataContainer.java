package de.retest.recheck.persistence.xml;

import java.io.Serializable;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.util.VersionProvider;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReTestXmlDataContainer<T extends Persistable> implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	/** package visible only for test */
	final String reTestVersion;

	@XmlAttribute
	/** package visible only for test */
	final String dataType;
	public static final String DATA_TYPE_FIELD = "dataType";

	@XmlAttribute
	/** package visible only for test */
	final int dataTypeVersion;
	public static final String DATA_TYPE_VERSION_FIELD = "dataTypeVersion";

	@XmlElement( type = Object.class )
	private final T data;

	@SuppressWarnings( "unused" ) // Only for JAXB
	private ReTestXmlDataContainer() {
		reTestVersion = null;
		dataType = null;
		dataTypeVersion = -1;
		data = null;
	}

	public ReTestXmlDataContainer( final T data ) {
		this.data = data;

		dataType = data.getClass().getCanonicalName();
		dataTypeVersion = data.version();

		reTestVersion = VersionProvider.RECHECK_VERSION;
	}

	public T data() {
		return data;
	}
}
