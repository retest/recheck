package de.retest.recheck;

import de.retest.recheck.persistence.FileNamer;

/**
 * Determines the paths under which Golden Master and test report files should be persisted. The default implementation
 * derives test suite and test case name from the test class and test method, working for both JUnit and TestNG.
 */
public interface FileNamerStrategy {

	/**
	 * Returns a {@link FileNamer}, that from given suitename, testname and checkname can construct a file.
	 *
	 * @return The {@link FileNamer} to use.
	 */
	FileNamer getFileNamer();

	/**
	 * @deprecated Because the old FileNamer mechanism was hard to understand and implement. Use {@link #getFileNamer()}
	 *             instead.
	 *
	 * @param baseNames
	 *            the base names to be used
	 * @return a file namer using the given base names
	 */
	@Deprecated
	default FileNamer createFileNamer( final String... baseNames ) {
		return getFileNamer();
	}

	/**
	 * Determines the suite name.
	 *
	 * @return name of the current test suite
	 */
	default String getSuiteName() {
		return getTestClassName();
	}

	/**
	 * Determines the test name.
	 *
	 * @return name of the current test
	 */
	default String getTestName() {
		return getTestMethodName();
	}

	/**
	 * @deprecated Use {@link #getSuiteName()} instead.
	 *
	 * @return The test class name from the JUnit call stack.
	 */
	@Deprecated
	default String getTestClassName() {
		return TestCaseFinder.getInstance() //
				.findTestCaseClassNameInStack() //
				.orElseThrow( () -> new IllegalStateException( "Couldn't identify test class in call stack.\n"
						+ "This is needed to dynamically name the Golden Master files.\n "
						+ "Please instantiate RecheckImpl with RecheckOptions, and provide a different FileNamerStrategy." ) );
	}

	/**
	 * @deprecated Use {@link #getTestName()} instead.
	 *
	 * @return The test method name from the JUnit call stack.
	 */
	@Deprecated
	default String getTestMethodName() {
		return TestCaseFinder.getInstance() //
				.findTestCaseMethodNameInStack() //
				.orElseThrow( () -> new IllegalStateException( "Couldn't identify test method in call stack.\n"
						+ "This is needed to dynamically name the Golden Master files.\n"
						+ "Please call `startTest(\"name\")`, giving an explicit name, "
						+ "or instantiate RecheckImpl with RecheckOptions, and provide a different FileNamerStrategy." ) );
	}

}
