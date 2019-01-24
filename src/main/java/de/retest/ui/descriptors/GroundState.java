package de.retest.ui.descriptors;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.retest.persistence.DateAdapter;

/**
 * GroundState contains all information that make individual runs of an application differ (such as execution date,
 * random seed, db-version, etc.).
 *
 * Upon creation, the GroundState gets a UUID, which is referenced by all graph. States can thus exist (be persisted,
 * remotely created and transmitted etc.) independent of the StateGraph and contain only the UUID of the GroundState and
 * thus the state relevant execution information.
 *
 * @author roessler
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class GroundState implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_EXECUTION_DATE = "de.retest.execution.Date";
	public static final String PROPERTY_EXECUTION_SEED = "de.retest.execution.randomSeed";

	public static final String UNSPECIFIED = "unspecified SUT version";

	// SimpleDateFormat is not thread-safe, so don't make it static
	private final transient SimpleDateFormat xmlDateFormat = new SimpleDateFormat( "dd.MM.yyyy-HH:mm:ss:SSS" );

	@XmlElement
	protected final String sutVersion;

	@XmlElement
	@XmlJavaTypeAdapter( DateAdapter.class )
	protected final Date executionDate;

	@XmlElement
	protected final long randomSeed;

	public GroundState() {
		this( UNSPECIFIED );
	}

	public GroundState( final String sutVersion ) {
		this.sutVersion = sutVersion;
		executionDate = getDateProperty();
		randomSeed = getRandomSeedProperty();
	}

	private long getRandomSeedProperty() {
		final String property = System.getProperty( PROPERTY_EXECUTION_SEED );
		if ( property == null ) {
			final long result = 1l;
			System.setProperty( PROPERTY_EXECUTION_SEED, Long.toString( result ) );
			return result;
		}
		return Long.parseLong( property );
	}

	private Date getDateProperty() {
		final String property = System.getProperty( PROPERTY_EXECUTION_DATE );
		if ( property == null ) {
			final Date result = new Date();
			System.setProperty( PROPERTY_EXECUTION_DATE, xmlDateFormat.format( result ) );
			return result;
		}
		try {
			return xmlDateFormat.parse( property );
		} catch ( final ParseException exc ) {
			throw new RuntimeException( "Exception parsing date '" + property + "'.", exc );
		}
	}

	public GroundState( final String sutVersion, final Date executionDate, final long randomSeed ) {
		this.sutVersion = sutVersion;
		this.executionDate = executionDate;
		this.randomSeed = randomSeed;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (executionDate == null ? 0 : executionDate.hashCode());
		result = prime * result + (int) (randomSeed ^ randomSeed >>> 32);
		result = prime * result + (sutVersion == null ? 0 : sutVersion.hashCode());
		return result;
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		final GroundState other = (GroundState) obj;
		if ( executionDate == null ) {
			if ( other.executionDate != null ) {
				return false;
			}
		} else if ( !executionDate.equals( other.executionDate ) ) {
			return false;
		}
		if ( randomSeed != other.randomSeed ) {
			return false;
		}
		if ( sutVersion == null ) {
			if ( other.sutVersion != null ) {
				return false;
			}
		} else if ( !sutVersion.equals( other.sutVersion ) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final String date = xmlDateFormat.format( executionDate );
		return "GroundState[sutVersion=" + sutVersion + ", executionDate=" + date + ", randomSeed=" + randomSeed + "l]";
	}

	public String getSutVersion() {
		return sutVersion;
	}
}
