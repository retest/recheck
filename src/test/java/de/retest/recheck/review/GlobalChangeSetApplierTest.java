package de.retest.recheck.review;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.ReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.AttributeChanges;
import de.retest.recheck.ui.review.ReviewResult;
import de.retest.recheck.ui.review.SuiteChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;

public class GlobalChangeSetApplierTest {

	private GlobalChangeSetApplier globalApplier;

	private IdentifyingAttributes identifyingAttributes;
	private AttributeDifference attributeDifference;

	private ReplayResult replayResult;
	private SuiteReplayResult suiteReplayResult;
	private TestReplayResult testReplayResult;
	private ActionReplayResult actionReplayResult1;
	private ActionReplayResult actionReplayResult2;
	private ElementDifference elementDifference1;
	private ElementDifference elementDifference2;

	private Element component;
	private ElementDifference insertedDifference;
	private InsertedDeletedElementDifference insertedLeafDifference;
	private ElementDifference deletedDifference;
	private InsertedDeletedElementDifference deletedLeafDifference;

	private ReviewResult reviewResult;
	private SuiteChangeSet suiteChangeSet;
	private TestChangeSet testChangeSet;
	private ActionChangeSet actionChangeSet1;
	private ActionChangeSet actionChangeSet2;
	private AttributeChanges identifyingAttributesChangeSet1;
	private AttributeChanges identifyingAttributesChangeSet2;
	private AttributeChanges attributeChangeSet1;
	private AttributeChanges attributeChangeSet2;

	@Before
	public void setUp() {
		identifyingAttributes = mock( IdentifyingAttributes.class );
		attributeDifference = mock( AttributeDifference.class );

		replayResult = mock( ReplayResult.class );
		suiteReplayResult = mock( SuiteReplayResult.class );
		testReplayResult = mock( TestReplayResult.class );
		actionReplayResult1 = mock( ActionReplayResult.class );
		actionReplayResult2 = mock( ActionReplayResult.class );
		elementDifference1 = mock( ElementDifference.class );
		elementDifference2 = mock( ElementDifference.class );

		component = mock( Element.class );
		insertedDifference = mock( ElementDifference.class );
		insertedLeafDifference = mock( InsertedDeletedElementDifference.class );
		deletedDifference = mock( ElementDifference.class );
		deletedLeafDifference = mock( InsertedDeletedElementDifference.class );

		when( replayResult.getSuiteReplayResults() ).thenReturn( Arrays.asList( suiteReplayResult ) );
		when( suiteReplayResult.getTestReplayResults() ).thenReturn( Arrays.asList( testReplayResult ) );
		when( testReplayResult.getActionReplayResults() ).thenReturn(
				Arrays.asList( actionReplayResult1, actionReplayResult2 ) );

		final List<ElementDifference> elementDifferences1 = Arrays.asList( elementDifference1, insertedDifference,
				deletedDifference );
		when( actionReplayResult1.getElementDifferences() ).thenReturn( elementDifferences1 );
		when( elementDifference1.getAttributeDifferences( null ) ).thenReturn( Arrays.asList( attributeDifference ) );
		when( elementDifference1.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final List<ElementDifference> elementDifferences2 = Arrays.asList( elementDifference2, insertedDifference,
				deletedDifference );
		when( actionReplayResult2.getElementDifferences() ).thenReturn( elementDifferences2 );
		when( elementDifference2.getAttributeDifferences( null ) ).thenReturn( Arrays.asList( attributeDifference ) );
		when( elementDifference2.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		when( component.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( insertedDifference.isInsertionOrDeletion() ).thenReturn( true );
		when( insertedDifference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( (InsertedDeletedElementDifference) insertedDifference.getIdentifyingAttributesDifference() ).thenReturn(
				insertedLeafDifference );
		when( insertedLeafDifference.isInserted() ).thenReturn( true );
		when( insertedLeafDifference.getActual() ).thenReturn( component );
		when( deletedDifference.isInsertionOrDeletion() ).thenReturn( true );
		when( deletedDifference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( (InsertedDeletedElementDifference) deletedDifference.getIdentifyingAttributesDifference() ).thenReturn(
				deletedLeafDifference );
		when( deletedLeafDifference.isInserted() ).thenReturn( false );

		reviewResult = mock( ReviewResult.class );
		suiteChangeSet = mock( SuiteChangeSet.class );
		testChangeSet = mock( TestChangeSet.class );
		actionChangeSet1 = mock( ActionChangeSet.class );
		actionChangeSet2 = mock( ActionChangeSet.class );
		identifyingAttributesChangeSet1 = mock( AttributeChanges.class );
		identifyingAttributesChangeSet2 = mock( AttributeChanges.class );
		attributeChangeSet1 = mock( AttributeChanges.class );
		attributeChangeSet2 = mock( AttributeChanges.class );
		when( reviewResult.createSuiteChangeSet( anyString(), anyString() ) ).thenReturn( suiteChangeSet );
		when( suiteChangeSet.createTestChangeSet() ).thenReturn( testChangeSet );
		when( testChangeSet.createActionChangeSet() ).thenReturn( actionChangeSet1 );
		when( actionChangeSet1.getIdentAttributeChanges() ).thenReturn( identifyingAttributesChangeSet1 );
		when( actionChangeSet1.getAttributesChanges() ).thenReturn( attributeChangeSet1 );
		when( actionChangeSet2.getIdentAttributeChanges() ).thenReturn( identifyingAttributesChangeSet2 );
		when( actionChangeSet2.getAttributesChanges() ).thenReturn( attributeChangeSet2 );

		globalApplier = GlobalChangeSetApplier.create( replayResult );
	}

	// Create.

	@Test
	public void create_should_read_all_elements_from_replayResult() {
		verify( replayResult, only() ).getSuiteReplayResults();
		verify( suiteReplayResult, only() ).getTestReplayResults();
		verify( testReplayResult, only() ).getActionReplayResults();
		verify( actionReplayResult1, only() ).getElementDifferences();
		verify( elementDifference1, times( 1 ) ).getAttributeDifferences( null );
		verify( elementDifference1, times( 1 ) ).getIdentifyingAttributes();
		verify( elementDifference1, times( 1 ) ).isInsertionOrDeletion();
		verifyNoMoreInteractions( elementDifference1 );
	}

	// Add/remove element differences.

	@Test
	public void add_for_all_ident_should_add_ident_change_set() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.addChangeSetForAllEqualIdentAttributeChanges( identifyingAttributes, attributeDifference );

		verify( identifyingAttributesChangeSet1, times( 1 ) ).add( identifyingAttributes, attributeDifference );
		verify( identifyingAttributesChangeSet2, times( 1 ) ).add( identifyingAttributes, attributeDifference );
	}

	@Test
	public void add_for_all_state_should_add_state_change_set() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.createChangeSetForAllEqualAttributesChanges( identifyingAttributes, attributeDifference );

		verify( attributeChangeSet1, times( 1 ) ).add( identifyingAttributes, attributeDifference );
		verify( attributeChangeSet2, times( 1 ) ).add( identifyingAttributes, attributeDifference );
	}

	@Test
	public void remove_for_all_ident_should_remove_ident_change_set() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.removeChangeSetForAllEqualIdentAttributeChanges( identifyingAttributes, attributeDifference );

		verify( identifyingAttributesChangeSet1, times( 1 ) ).remove( identifyingAttributes, attributeDifference );
		verify( identifyingAttributesChangeSet2, times( 1 ) ).remove( identifyingAttributes, attributeDifference );
	}

	@Test
	public void remove_for_all_state_should_remove_state_change_set() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.removeChangeSetForAllEqualAttributesChanges( identifyingAttributes, attributeDifference );

		verify( attributeChangeSet1, atLeastOnce() ).remove( identifyingAttributes, attributeDifference );
		verify( attributeChangeSet2, atLeastOnce() ).remove( identifyingAttributes, attributeDifference );
	}

	@Test( expected = AssertionError.class )
	public void missing_introduce_should_yield_AssertionError() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		// Intentionally missing introduce call for actionReplayResult2 and actionChangeSet2.

		globalApplier.removeChangeSetForAllEqualAttributesChanges( identifyingAttributes, attributeDifference );
	}

	// Add/remove inserted/deleted differences.

	@Test
	public void add_inserted_difference_should_add_inserted_change_set_for_all() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.addChangeSetForAllEqualInsertedChanges( component );

		verify( actionChangeSet1, times( 1 ) ).addInsertChange( component );
		verify( actionChangeSet2, times( 1 ) ).addInsertChange( component );
	}

	@Test
	public void add_deleted_difference_should_add_deleted_change_set_for_all() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.addChangeSetForAllEqualDeletedChanges( identifyingAttributes );

		verify( actionChangeSet1, times( 1 ) ).addDeletedChange( identifyingAttributes );
		verify( actionChangeSet2, times( 1 ) ).addDeletedChange( identifyingAttributes );
	}

	@Test
	public void remove_inserted_difference_should_remove_inserted_change_set_for_all() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.removeChangeSetForAllEqualInsertedChanges( component );

		verify( actionChangeSet1, times( 1 ) ).removeInsertChange( component );
		verify( actionChangeSet2, times( 1 ) ).removeInsertChange( component );
	}

	@Test
	public void remove_deleted_difference_should_remove_deleted_change_set_for_all() throws Exception {
		globalApplier.introduce( actionReplayResult1, actionChangeSet1 );
		globalApplier.introduce( actionReplayResult2, actionChangeSet2 );

		globalApplier.removeChangeSetForAllEqualDeletedChanges( identifyingAttributes );

		verify( actionChangeSet1, times( 1 ) ).removeDeletedChange( identifyingAttributes );
		verify( actionChangeSet2, times( 1 ) ).removeDeletedChange( identifyingAttributes );
	}

}
