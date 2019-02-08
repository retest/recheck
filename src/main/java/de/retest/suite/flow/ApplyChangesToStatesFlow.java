package de.retest.suite.flow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.persistence.NoStateFileFoundException;
import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.persistence.RecheckStateFileProvider;
import de.retest.recheck.persistence.RecheckStateFileProviderImpl;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.SuiteChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;

public class ApplyChangesToStatesFlow {

	private static final Logger logger = LoggerFactory.getLogger( ApplyChangesToStatesFlow.class );

	private final RecheckStateFileProvider recheckStateFileProvider;

	public static List<String> apply( final Persistence<SutState> persistence, final SuiteChangeSet acceptedChanges )
			throws NoStateFileFoundException {
		return new ApplyChangesToStatesFlow( persistence ).apply( acceptedChanges );
	}

	private ApplyChangesToStatesFlow( final Persistence<SutState> persistence ) {
		recheckStateFileProvider = new RecheckStateFileProviderImpl( persistence );
	}

	private List<String> apply( final SuiteChangeSet acceptedChanges ) throws NoStateFileFoundException {
		final List<String> updatedFiles = new ArrayList<>();
		for ( final TestChangeSet testChangeSet : acceptedChanges.getTestChangeSets() ) {
			updatedFiles.addAll( apply( testChangeSet ) );
		}
		return updatedFiles;
	}

	private List<String> apply( final TestChangeSet testChangeSet ) throws NoStateFileFoundException {
		if ( testChangeSet.isEmpty() ) {
			return Collections.emptyList();
		}
		final List<String> updatedFiles = new ArrayList<>();
		for ( final ActionChangeSet changeSet : testChangeSet.getActionChangeSets() ) {
			updatedFiles.addAll( apply( changeSet ) );
		}
		// TODO: RET-1274 will remove the initial change set
		if ( testChangeSet.containsInitialStateChangeSet() ) {
			final ActionChangeSet changeSet = testChangeSet.getInitialStateChangeSet();
			updatedFiles.addAll( apply( changeSet ) );
		}
		return updatedFiles;
	}

	private List<String> apply( final ActionChangeSet actionChangeSet ) throws NoStateFileFoundException {
		if ( actionChangeSet.isEmpty() ) {
			return Collections.emptyList();
		}
		final File file = recheckStateFileProvider.getRecheckStateFile( actionChangeSet.getStateFilePath() );
		final SutState oldState = recheckStateFileProvider.loadRecheckState( file );
		final SutState newState = oldState.applyChanges( actionChangeSet );
		if ( newState.equals( oldState ) ) {
			logger.debug( "SutState {} did not change after applying changes, so not persisting it...", oldState );
			return Collections.emptyList();
		}
		recheckStateFileProvider.saveRecheckState( file, newState );
		return Collections.singletonList( actionChangeSet.getDescription() );
	}
}
