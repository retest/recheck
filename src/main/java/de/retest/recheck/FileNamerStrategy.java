package de.retest.recheck;

import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.persistence.NamingStrategy;
import de.retest.recheck.persistence.ProjectLayout;

/**
 * @deprecated Use {@link NamingStrategy} and {@link ProjectLayout} instead.
 */
@Deprecated
public interface FileNamerStrategy extends NamingStrategy {

	@Override
	default String getSuiteName() {
		return getTestClassName();
	}

	@Override
	default String getTestName() {
		return getTestMethodName();
	}

	/**
	 * Creates a file namer for the given base names. For Golden Master files, these typically follow the format
	 * <code>${TEST_CLASS_NAME}/${TEST_METHOD_NAME}.${STEP_NAME}</code>, whereas test report files only use
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
		return TestCaseFinder.getInstance() //
				.findTestCaseClassNameInStack() //
				.orElseThrow( () -> new IllegalStateException( "Couldn't identify test class in call stack.\n"
						+ "This is needed to dynamically name the Golden Master files.\n "
						+ "Note that FileNamerStrategy is deprecated.\n"
						+ "Therefore, please instantiate RecheckImpl with RecheckOptions, and provide a corresponding NamingStrategy." ) );
	}

	/**
	 * Determines the current test method name.
	 *
	 * @return name of the current test method
	 */
	default String getTestMethodName() {
		return TestCaseFinder.getInstance() //
				.findTestCaseMethodNameInStack() //
				.orElseThrow( () -> new IllegalStateException( "Couldn't identify test method in call stack.\n"
						+ "This is needed to dynamically name the Golden Master files.\n"
						+ "Note that FileNamerStrategy is deprecated.\n"
						+ "Therefore, please call `startTest(\"name\")`, giving an explicit name, "
						+ "or instantiate RecheckImpl with RecheckOptions, and provide a corresponding NamingStrategy." ) );
	}

}
