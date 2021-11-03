package de.retest.recheck.suite;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Objects;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.ui.actions.ActionStateSequence;
import de.retest.recheck.ui.descriptors.GroundState;
import de.retest.recheck.ui.review.SuiteChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ExecutableSuite extends Persistable {

	private static final long serialVersionUID = 4L;
	private static final int PERSISTENCE_VERSION = 23;

	@XmlAttribute
	private final String uuid;

	@XmlElement
	private final List<ActionStateSequence> tests = new ArrayList<>();

	@XmlElement
	private final GroundState groundState;

	@XmlElement
	private final long sumDuration;

	private String name;

	private ExecutableSuite() {
		super( PERSISTENCE_VERSION );
		groundState = null;
		sumDuration = 0L;
		uuid = null;
	}

	public ExecutableSuite( final GroundState groundState, final long sumDuration,
			final List<ActionStateSequence> tests ) {
		super( PERSISTENCE_VERSION );
		this.groundState = groundState;
		this.sumDuration = sumDuration;
		uuid = UUID.randomUUID().toString();
		addTests( tests );
	}

	public List<ActionStateSequence> getTests() {
		return tests;
	}

	public void addTests( final List<ActionStateSequence> actionStateSequences ) {
		tests.addAll( actionStateSequences );
	}

	public String getName() {
		return name;
	}

	public void setName( final String name ) {
		this.name = name;
	}

	public GroundState getGroundState() {
		return groundState;
	}

	public long getSumDuration() {
		return sumDuration;
	}

	public ExecutableSuite applyChanges( final SuiteChangeSet suiteChangeSet )
			throws ExecutionSuitesDoNotMatchException {
		if ( !Objects.equal( suiteChangeSet.getUuid(), uuid ) ) {
			throw new ExecutionSuitesDoNotMatchException( uuid, suiteChangeSet.getUuid() );
		}
		final List<ActionStateSequence> newTests = new ArrayList<>();

		// usage of index is imported here, because need a mapping change set to test.
		// Other value as index are not useful, because a suite can contain a test multiple times.
		for ( int i = 0; i < tests.size(); i++ ) {
			newTests.add( manipulate( tests.get( i ), suiteChangeSet.getTestChangeSet( i ) ) );
		}

		return new ExecutableSuite( groundState, sumDuration, newTests );
	}

	private static ActionStateSequence manipulate( final ActionStateSequence test, final TestChangeSet testChangeSet ) {
		if ( testChangeSet != null ) {
			return test.applyChanges( testChangeSet );
		} else {
			return test;
		}
	}

	public String getUuid() {
		return uuid;
	}

}
