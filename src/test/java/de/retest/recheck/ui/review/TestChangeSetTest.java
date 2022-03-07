package de.retest.recheck.ui.review;

import static de.retest.recheck.ui.review.ChangeSetTestUtils.fillActionChangeSet;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TestChangeSetTest {

	TestChangeSet testChangeSet;

	@Before
	public void setUp() throws Exception {
		testChangeSet = new TestChangeSet();
	}

	@Test
	public void createActionChangeSet_returns_ActionChangeSet() {
		assertThat( testChangeSet.createActionChangeSet() ).isNotNull();
	}

	@Test
	public void getInitialStateChangeSet_returns_not_null() {
		assertThat( testChangeSet.getInitialStateChangeSet() ).isNotNull();
	}

	@Test
	public void getTestChangeSet_returns_created_testChangeSet() {
		final ActionChangeSet actionChangeSet = testChangeSet.createActionChangeSet();
		fillActionChangeSet( actionChangeSet );
		assertThat( testChangeSet.getActionChangeSet( 0 ) ).isEqualTo( actionChangeSet );
	}

	@Test
	public void getTestChangeSet_returns_only_non_empty_testChangeSets() {
		testChangeSet.createActionChangeSet();
		final ActionChangeSet nonEmptyActionChangeSet = testChangeSet.createActionChangeSet();
		fillActionChangeSet( nonEmptyActionChangeSet );
		assertThat( testChangeSet.getActionChangeSet( 0 ) ).isNull();
		assertThat( testChangeSet.getActionChangeSet( 1 ) ).isEqualTo( nonEmptyActionChangeSet );
	}

	@Test
	public void containsInitialStateChangeSet_returns_only_true_when_changeSets_exists() {
		assertThat( testChangeSet.containsInitialStateChangeSet() ).isFalse();
		fillActionChangeSet( testChangeSet.getInitialStateChangeSet() );
		assertThat( testChangeSet.containsInitialStateChangeSet() ).isTrue();
	}

	@Test
	public void isEmpty_returns_true_per_default() {
		assertThat( testChangeSet.isEmpty() ).isTrue();
	}

	@Test
	public void isEmpty_returns_false_when_initialStateChangeSet_is_not_empty() {
		fillActionChangeSet( testChangeSet.getInitialStateChangeSet() );
		assertThat( testChangeSet.isEmpty() ).isFalse();
	}

	@Test
	public void isEmpty_returns_false_when_actionChangeSets_are_not_empty() {
		fillActionChangeSet( testChangeSet.createActionChangeSet() );
		assertThat( testChangeSet.isEmpty() ).isFalse();
	}

}
