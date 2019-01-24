package de.retest.ui.review;

import static de.retest.ui.review.ChangeSetTestUtils.fillTestChangeSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class SuiteChangeSetTest {

	SuiteChangeSet suiteChangeSet;
	String suiteName;

	@Before
	public void setUp() throws Exception {
		suiteName = new Date().toString();
		suiteChangeSet = new SuiteChangeSet( suiteName, "uuid" );
	}

	@Test
	public void createTestChangeSet_returns_TestChangeSet() {
		assertThat( suiteChangeSet.createTestChangeSet() ).isNotNull();
	}

	@Test
	public void getSuiteName_returns_name_from_constructor() {
		assertThat( suiteChangeSet.getSuiteName() ).isEqualTo( suiteName );
	}

	@Test
	public void getTestChangeSet_returns_created_testChangeSet() {
		final TestChangeSet testChangeSet = suiteChangeSet.createTestChangeSet();
		fillTestChangeSet( testChangeSet );
		assertThat( suiteChangeSet.getTestChangeSet( 0 ) ).isEqualTo( testChangeSet );
	}

	@Test
	public void getTestChangeSet_returns_in_correct_order() {
		final TestChangeSet testChangeSet0 = suiteChangeSet.createTestChangeSet();
		fillTestChangeSet( testChangeSet0 );
		final TestChangeSet testChangeSet1 = suiteChangeSet.createTestChangeSet();
		fillTestChangeSet( testChangeSet1 );
		final TestChangeSet testChangeSet2 = suiteChangeSet.createTestChangeSet();
		fillTestChangeSet( testChangeSet2 );
		final TestChangeSet testChangeSet3 = suiteChangeSet.createTestChangeSet();
		fillTestChangeSet( testChangeSet3 );

		assertThat( suiteChangeSet.getTestChangeSet( 0 ) ).isEqualTo( testChangeSet0 );
		assertThat( suiteChangeSet.getTestChangeSet( 3 ) ).isEqualTo( testChangeSet3 );
		assertThat( suiteChangeSet.getTestChangeSet( 2 ) ).isEqualTo( testChangeSet2 );
		assertThat( suiteChangeSet.getTestChangeSet( 1 ) ).isEqualTo( testChangeSet1 );
	}

	@Test
	public void getTestChangeSet_returns_only_non_empty_testChangeSets() {
		suiteChangeSet.createTestChangeSet();
		final TestChangeSet nonEmptyTestChangeSet = suiteChangeSet.createTestChangeSet();
		fillTestChangeSet( nonEmptyTestChangeSet );
		assertThat( suiteChangeSet.getTestChangeSet( 0 ) ).isNull();
		assertThat( suiteChangeSet.getTestChangeSet( 1 ) ).isEqualTo( nonEmptyTestChangeSet );
	}

}
