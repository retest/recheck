package de.retest.report;

import static de.retest.ui.review.GoldenMasterSource.RECORDED;

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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Joiner;

import de.retest.ExecutingTestContext;
import de.retest.elementcollection.ElementCollection;
import de.retest.elementcollection.RecheckIgnore;
import de.retest.persistence.GoldenMasterSourceSuppressDefaultAdapter;
import de.retest.persistence.Persistable;
import de.retest.ui.review.GoldenMasterSource;

@XmlRootElement( name = "testreport" )
@XmlAccessorType( XmlAccessType.FIELD )
public class ReplayResult extends Persistable {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 18;

	@XmlElement
	private final ElementCollection ignoredElements;

	@XmlElement( name = "suite" )
	private final List<SuiteReplayResult> suiteReplayResults = new ArrayList<>();

	@XmlAttribute
	@XmlJavaTypeAdapter( GoldenMasterSourceSuppressDefaultAdapter.class )
	private final GoldenMasterSource source;

	public static ReplayResult fromApi( final SuiteReplayResult newSuite ) {
		return new ReplayResult( GoldenMasterSource.API, newSuite );
	}

	public ReplayResult() {
		super( PERSISTENCE_VERSION );
		source = RECORDED;
		ignoredElements = RecheckIgnore.getInstance().getIgnored();
	}

	public ReplayResult( final SuiteReplayResult newSuite ) {
		this( GoldenMasterSource.RECORDED, newSuite );
	}

	public ReplayResult( final GoldenMasterSource source, final SuiteReplayResult newSuite ) {
		super( PERSISTENCE_VERSION );
		this.source = source;
		suiteReplayResults.add( newSuite );
		ignoredElements = RecheckIgnore.getInstance().getIgnored();
	}

	public void addSuite( final SuiteReplayResult newReplayResult ) {
		suiteReplayResults.add( newReplayResult );
	}

	public List<SuiteReplayResult> getSuiteReplayResults() {
		return Collections.unmodifiableList( suiteReplayResults );
	}

	public boolean containsChanges() {
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			if ( suiteReplayResult.getDifferencesCount() > 0 ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return Joiner.on( "\n" ).join( suiteReplayResults );
	}

	public int getNumberOfTests() {
		int testCount = 0;
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			testCount += suiteReplayResult.getTestReplayResults().size();
		}
		return testCount;
	}

	public int getNumberOfActions() {
		int actionsCount = 0;
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			actionsCount += suiteReplayResult.getNumberOfActions();
		}
		return actionsCount;
	}

	public long getDuration() {
		long duration = 0;
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			duration += suiteReplayResult.getDuration();
		}
		return duration;
	}

	public int getCheckedUiElementsCount() {
		int uiElementsCount = 0;
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			uiElementsCount += suiteReplayResult.getCheckedUiElementsCount();
		}
		return uiElementsCount;
	}

	public int getDifferencesCount() {
		int differences = 0;
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			differences += suiteReplayResult.getDifferencesCount();
		}
		return differences;
	}

	public int getErrorsCount() {
		int errors = 0;
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			errors += suiteReplayResult.getErrorsCount();
		}
		return errors;
	}

	public String getExecSuiteSutVersion() {
		final Set<String> versions = new HashSet<>();
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			versions.add( suiteReplayResult.getExecSuiteSutVersion() );
		}
		if ( versions.size() == 1 ) {
			return versions.toArray()[0].toString();
		}
		return versions.toString();
	}

	public String getReplaySutVersion() {
		final Set<String> versions = new HashSet<>();
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			versions.add( suiteReplayResult.getReplaySutVersion() );
		}
		if ( versions.size() == 1 ) {
			return versions.toArray()[0].toString();
		}
		return versions.toString();
	}

	public boolean isEmpty() {
		return getNumberOfTestsWithChanges() == 0;
	}

	public int getNumberOfTestsWithChanges() {
		int result = 0;
		for ( final SuiteReplayResult suiteReplayResult : suiteReplayResults ) {
			result += suiteReplayResult.getNumberOfTestsWithChanges();
		}
		return result;
	}

	public RecheckIgnore getLocalRecheckIgnore( final ExecutingTestContext context ) {
		return new RecheckIgnore( ignoredElements, context.getIgnoredTypes() );
	}

	public GoldenMasterSource getGoldenMasterSource() {
		return source;
	}
}
