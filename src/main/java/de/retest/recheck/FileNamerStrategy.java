package de.retest.recheck;

import de.retest.recheck.persistence.FileNamer;

/**
 * Determines the paths under which Golden Master and result files should be persisted.
 */
public interface FileNamerStrategy {

	/**
	 * Creates a file namer for the given base names. For Golden Master files, these typically follow the format
	 * <code>${TEST_CLASS_NAME}/${TEST_METHOD_NAME}.${STEP_NAME}</code>, whereas result files only use
	 * <code>${TEST_CLASS_NAME}</code>.
	 *
	 * @param baseNames
	 *            the base names to be used
	 * @return a file namer using the given base names
	 */
	FileNamer createFileNamer( String... baseNames );

	/**
	 * Determines the current test class name.
	 *
	 * @return name of the current test class
	 */
	default String getTestClassName() {
		final StackTraceElement testMethodStack = TestCaseFinder.findTestCaseMethodInStack();
		if ( testMethodStack != null ) {
			return testMethodStack.getClassName();
		}
		throw new RuntimeException( "Couldn't identify test class in call stack. \n"
				+ "This name is needed to dynamically name the Golden Master files. \n "
				+ "Please instantiate RecheckImpl with RecheckOptions, and provide a different FileNamerStrategy." );
	}

	/**
	 * Determines the current test method name.
	 *
	 * @return name of the current test method
	 */
	default String getTestMethodName() {
		final StackTraceElement testMethodStack = TestCaseFinder.findTestCaseMethodInStack();
		if ( testMethodStack != null ) {
			return testMethodStack.getMethodName();
		}
		throw new RuntimeException( "Couldn't identify test method in call stack. \n"
				+ "This name is needed to dynamically name the Golden Master files. \n"
				+ "Please instantiate RecheckImpl with RecheckOptions, and provide a different FileNamerStrategy." );
	}

}
