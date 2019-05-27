package de.retest.recheck.report;

import static de.retest.recheck.persistence.xml.util.XmlUtil.clean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.descriptors.GroundState;

@XmlRootElement( name = "suite" )
@XmlAccessorType( XmlAccessType.FIELD )
public class SuiteReplayResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( SuiteReplayResult.class );

	@XmlAttribute
	private final String name;

	@XmlAttribute
	private final String suiteUuid;

	@XmlAttribute
	private final int suiteNr;

	@XmlElement( name = "test" )
	private List<TestReplayResult> testReplayResults = new ArrayList<>();

	@XmlElement
	private long suiteDuration;

	@XmlElement
	private final GroundState execSuiteSutVersion;

	@XmlElement
	private final GroundState replaySutVersion;

	@XmlElement
	private final ExecutableSuite suite;

	@SuppressWarnings( "unused" )
	private SuiteReplayResult() {
		// for JAXB
		suiteNr = 0;
		name = null;
		execSuiteSutVersion = null;
		replaySutVersion = null;
		suiteUuid = null;
		suite = null;
	}

	public SuiteReplayResult( final ExecutableSuite suite, final int suiteNr, final GroundState replaySutVersion ) {
		name = suite.getName() == null ? "Suite no. " + suiteNr : clean( suite.getName() );
		if ( suite.getName() == null ) {
			logger.info( "No suite name given, using {}.", name );
		}
		this.suiteNr = suiteNr;
		// Don't use here a list without a fix ordering, because we need the exact order
		// for applyChanges in review module!!
		testReplayResults = new ArrayList<>();
		execSuiteSutVersion = suite.getGroundState();
		suiteUuid = suite.getUuid();
		this.replaySutVersion = replaySutVersion;
		this.suite = suite;
	}

	public void addTest( final TestReplayResult newReplayResult ) {
		suiteDuration += newReplayResult.getDuration();
		testReplayResults.add( newReplayResult );
	}

	public int getSuiteNr() {
		return suiteNr;
	}

	public String getSuiteName() {
		return name;
	}

	public long getDuration() {
		return suiteDuration;
	}

	public String getExecSuiteSutVersion() {
		if ( execSuiteSutVersion == null ) {
			return GroundState.UNSPECIFIED;
		}
		return execSuiteSutVersion.getSutVersion();
	}

	public GroundState getGroundState() {
		return replaySutVersion;
	}

	public String getReplaySutVersion() {
		if ( replaySutVersion == null ) {
			return "unspecified SUT version";
		}
		return replaySutVersion.getSutVersion();
	}

	public ExecutableSuite getSuite() {
		return suite;
	}

	public List<TestReplayResult> getTestReplayResults() {
		return Collections.unmodifiableList( testReplayResults );
	}

	public int getNumberOfActions() {
		int actionCount = 0;
		for ( final TestReplayResult testReplayResult : testReplayResults ) {
			actionCount += testReplayResult.getActionReplayResults().size();
		}
		return actionCount;
	}

	public int getDifferencesCount() {
		int diffsCount = 0;
		for ( final TestReplayResult testReplayResult : testReplayResults ) {
			for ( final ActionReplayResult actionReplayResult : testReplayResult.getActionReplayResults() ) {
				diffsCount += actionReplayResult.getAllElementDifferences().size();
			}
		}
		return diffsCount;
	}

	public int getErrorsCount() {
		int errorsCount = 0;
		for ( final TestReplayResult testReplayResult : testReplayResults ) {
			for ( final ActionReplayResult actionReplayResult : testReplayResult.getActionReplayResults() ) {
				if ( actionReplayResult.getThrowableWrapper() != null
						|| actionReplayResult.getTargetNotFoundWrapper() != null ) {
					errorsCount++;
				}
			}
		}
		return errorsCount;
	}

	public int getCheckedUiElementsCount() {
		int uiElementsCount = 0;
		for ( final TestReplayResult testReplayResult : testReplayResults ) {
			uiElementsCount += testReplayResult.getCheckedUiElementsCount();
		}
		return uiElementsCount;
	}

	@Override
	public String toString() {
		return "SuiteReplayResult('" + getSuiteName() + "'" //
				+ ", Actions: " + getNumberOfActions() //
				+ ", Checked Elements: " + getCheckedUiElementsCount() //
				+ ", Differences: " + getDifferencesCount() //
				+ ", Errors: " + getErrorsCount() //
				+ ")";
	}

	public String getSuiteUuid() {
		return suiteUuid;
	}

	public boolean isEmpty() {
		return getNumberOfTestsWithChanges() != 0;
	}

	public int getNumberOfTestsWithChanges() {
		int result = 0;
		for ( final TestReplayResult testReplayResult : testReplayResults ) {
			if ( !testReplayResult.isEmpty() ) {
				result++;
			}
		}
		return result;
	}
}
