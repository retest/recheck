package de.retest.recheck;

import java.io.File;

import com.google.common.base.Joiner;

import de.retest.persistence.FileNamer;

public class MavenConformFileNamerStrategy implements FileNamerStrategy {

	public static final String WORKSPACE_DEFAULT = "src/test/resources/retest/";

	@Override
	public FileNamer createFileNamer( final String... baseNames ) {
		return new FileNamer() {

			@Override
			public File getFile( final String extension ) {
				final String baseName = Joiner.on( "/" ).join( baseNames );
				return new File( WORKSPACE_DEFAULT + File.separator + RECHECK_FOLDER_NAME + File.separator + baseName
						+ extension );
			}

			@Override
			public File getResultFile( final String extension ) {
				final String baseName = Joiner.on( "/" ).join( baseNames );
				return new File( "target/test-classes/retest/" + File.separator + RECHECK_FOLDER_NAME
						+ File.separator + baseName + extension );
			}
		};
	}

	@Override
	public String getTestClassName() {
		final StackTraceElement testMethodStack = TestCaseFinder.findTestCaseMethodInStack();
		if ( testMethodStack != null ) {
			final String className = testMethodStack.getClassName();
			return className;
		}
		throw new RuntimeException( "Couldn't identify test method in call stack. Use explicit namer!" );
	}

	@Override
	public String getTestMethodName() {
		final StackTraceElement testMethodStack = TestCaseFinder.findTestCaseMethodInStack();
		if ( testMethodStack != null ) {
			return testMethodStack.getMethodName();
		}
		throw new RuntimeException( "Couldn't identify test method in call stack. Use explicit namer!" );
	}
}
