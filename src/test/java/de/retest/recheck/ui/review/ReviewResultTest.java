package de.retest.recheck.ui.review;

import static de.retest.recheck.ui.review.ChangeSetTestUtils.fillSuiteChangeSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ReviewResultTest {

	private ReviewResult reviewResult;

	@Before
	public void setUp() {
		reviewResult = new ReviewResult();
	}

	@Test
	public void createSuiteChangeSet_returns_SuiteChangeSet() throws Exception {
		assertThat( reviewResult.createSuiteChangeSet( "testName", "uuid" ) ).isNotNull();
	}

	@Test
	public void getSuiteChangeSets_returns_a_created_suite_changeset() throws Exception {
		final SuiteChangeSet suiteChangeSet = reviewResult.createSuiteChangeSet( "testName", "uuid" );
		fillSuiteChangeSet( suiteChangeSet );

		assertThat( reviewResult.getSuiteChangeSets() ).isNotEmpty();
		assertThat( reviewResult.getSuiteChangeSets() ).contains( suiteChangeSet );
	}

	@Test
	public void getSuiteChangeSets_returns_only_non_empty_changeSets() {
		final SuiteChangeSet empySuiteChangeSet = reviewResult.createSuiteChangeSet( "empty", "uuid" );
		final SuiteChangeSet nonEmpySuiteChangeSet = reviewResult.createSuiteChangeSet( "nonEmpy", "uuid" );
		fillSuiteChangeSet( nonEmpySuiteChangeSet );

		assertThat( reviewResult.getSuiteChangeSets() ).containsOnly( nonEmpySuiteChangeSet );
		assertThat( reviewResult.getSuiteChangeSets() ).doesNotContain( empySuiteChangeSet );
	}

	@Test
	public void getSuiteChangeSets_returns_changeSets_in_correct_order() throws Exception {
		fillSuiteChangeSet( reviewResult.createSuiteChangeSet( "0", "uuid" ) );
		fillSuiteChangeSet( reviewResult.createSuiteChangeSet( "1", "uuid" ) );
		fillSuiteChangeSet( reviewResult.createSuiteChangeSet( "2", "uuid" ) );
		fillSuiteChangeSet( reviewResult.createSuiteChangeSet( "3", "uuid" ) );
		fillSuiteChangeSet( reviewResult.createSuiteChangeSet( "4", "uuid" ) );

		final List<SuiteChangeSet> suiteChangeSets = reviewResult.getSuiteChangeSets();
		for ( int i = 0; i < 5; i++ ) {
			assertThat( suiteChangeSets.get( i ).getSuiteName() ).isEqualTo( "" + i );
		}
	}

}
