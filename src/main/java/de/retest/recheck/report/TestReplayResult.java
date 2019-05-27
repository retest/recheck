package de.retest.recheck.report;

import static de.retest.recheck.persistence.xml.util.XmlUtil.clean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.diff.LeafDifference;

@XmlRootElement( name = "test" )
@XmlAccessorType( XmlAccessType.FIELD )
public class TestReplayResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( TestReplayResult.class );

	@XmlAttribute
	private final String name;

	@XmlAttribute
	private final int testNr;

	@XmlElement( name = "action" )
	private List<ActionReplayResult> actionReplayResults;

	@XmlElement
	private long testDuration;

	@SuppressWarnings( "unused" )
	private TestReplayResult() {
		// for JAXB
		testNr = 0;
		name = null;
	}

	public TestReplayResult( final String testName, final int testNr ) {
		name = testName == null ? "Test no. " + testNr : clean( testName );
		if ( testName == null ) {
			logger.info( "No test name given, using {}.", name );
		}
		this.testNr = testNr;
		// Don't use here a list without a fix ordering, because we need the exact order
		// for applyChanges in review module!!
		actionReplayResults = new ArrayList<>();
	}

	public int getTestNr() {
		return testNr;
	}

	public long getDuration() {
		return testDuration;
	}

	public void addAction( final ActionReplayResult newReplayResult ) {
		testDuration += newReplayResult.getDuration();
		actionReplayResults.add( newReplayResult );
	}

	public String getName() {
		return name;
	}

	public List<ActionReplayResult> getActionReplayResults() {
		return Collections.unmodifiableList( actionReplayResults );
	}

	public int getCheckedUiElementsCount() {
		int uiElementsCount = 0;
		for ( final ActionReplayResult actionReplayResult : actionReplayResults ) {
			uiElementsCount += actionReplayResult.getCheckedUiElementsCount();
		}
		return uiElementsCount;
	}

	public Set<LeafDifference> getDifferences( final Filter filter ) {
		final Set<LeafDifference> diffs = new HashSet<>();
		for ( final ActionReplayResult actionReplayResult : actionReplayResults ) {
			diffs.addAll( actionReplayResult.getDifferencesWithout( filter ) );
		}
		return diffs;
	}

	public int getDifferencesCount() {
		int differences = 0;
		for ( final ActionReplayResult actionReplayResult : actionReplayResults ) {
			differences += actionReplayResult.getAllElementDifferences().size();
		}
		return differences;
	}

	public boolean hasNoGoldenMaster() {
		for ( final ActionReplayResult result : actionReplayResults ) {
			if ( !(result instanceof NoGoldenMasterActionReplayResult) ) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "TestReplayResult('" + getName() + "'" //
				+ ", Actions: " + getActionReplayResults().size() //
				+ ", Checked Elements: " + getCheckedUiElementsCount() //
				+ ", Differences: " + getDifferencesCount() //
				+ ")";
	}

	public boolean isEmpty() {
		return actionReplayResults.isEmpty();
	}
}
