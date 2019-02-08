package de.retest.recheck.suite;

public class ExecutionSuitesDoNotMatchException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExecutionSuitesDoNotMatchException( final String expectedUuid, final String actualUuid ) {
		super( "The given change set does not match this execsuite - has it already been updated? expectedUuid:"
				+ expectedUuid + ", actualUuid:" + actualUuid );
	}

}
