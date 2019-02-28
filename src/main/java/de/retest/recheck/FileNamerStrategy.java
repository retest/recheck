package de.retest.recheck;

import de.retest.recheck.persistence.FileNamer;

public interface FileNamerStrategy {

	static final String RECHECK_FILE_EXTENSION = ".recheck";
	static final String RECHECK_FOLDER_NAME = "recheck";

	FileNamer createFileNamer( String... baseNames );

	default String getTestClassName() {
		final StackTraceElement testMethodStack = TestCaseFinder.findTestCaseMethodInStack();
		if ( testMethodStack != null ) {
			return testMethodStack.getClassName();
		}
		throw new RuntimeException( "Couldn't identify test method in call stack. Use explicit namer!" );
	}

	default String getTestMethodName() {
		final StackTraceElement testMethodStack = TestCaseFinder.findTestCaseMethodInStack();
		if ( testMethodStack != null ) {
			return testMethodStack.getMethodName();
		}
		throw new RuntimeException( "Couldn't identify test method in call stack. Use explicit namer!" );
	}

}
