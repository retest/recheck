package de.retest.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbortableCompoundStoppingCondition implements TestExecutionStoppingCondition {

	private static final org.slf4j.Logger logger =
			org.slf4j.LoggerFactory.getLogger( AbortableCompoundStoppingCondition.class );

	private final List<TestExecutionStoppingCondition> delegates = new ArrayList<>();
	private boolean aborted = false;

	public AbortableCompoundStoppingCondition( final TestExecutionStoppingCondition... delegates ) {
		this.delegates.addAll( Arrays.asList( delegates ) );
	}

	@Override
	public boolean finished() {
		if ( aborted ) {
			return true;
		}
		for ( final TestExecutionStoppingCondition condition : delegates ) {
			if ( condition.finished() ) {
				logger.info( "{} says test execution is finished.", condition );
				return true;
			}
		}
		return false;
	}

	public void abort( final String reason ) {
		logger.info( "Abortion of test execution triggered: {}", reason );
		aborted = true;
	}
}
