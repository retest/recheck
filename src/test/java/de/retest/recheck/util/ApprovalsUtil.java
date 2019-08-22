package de.retest.recheck.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.approvaltests.Approvals;
import org.approvaltests.core.ApprovalFailureReporter;
import org.approvaltests.namer.ApprovalNamer;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.writers.ApprovalTextWriter;

import com.spun.util.io.StackElementSelector;
import com.spun.util.tests.StackTraceReflectionResult;
import com.spun.util.tests.TestUtils;

import de.retest.recheck.TestCaseFinder;

public class ApprovalsUtil {

	private static final String UNSPECIFIED_DEVELOPMENT_VERSION = "unspecified development version";

	/**
	 * For automatic approval use {@link ApprovalAutoApprover#INSTANCE}.
	 */
	private static final ApprovalFailureReporter DIFF_HANDLER = DiffReporter.INSTANCE;
	//	private static final ApprovalFailureReporter DIFF_HANDLER = ApprovalAutoApprover.INSTANCE;

	public static void verifyXml( final String actual ) {
		verify( maskVersion( actual ), "xml" );
	}

	public static void verify( final Object o ) {
		verify( o.toString(), "txt" );
	}

	private static void verify( final String actual, final String fileExtensionWithoutDot ) {
		Approvals.verify( new ApprovalTextWriter( actual, fileExtensionWithoutDot ),
				new MavenConformJUnitStackTraceNamer(), DIFF_HANDLER );
	}

	private static String maskVersion( final String actual ) {
		final Pattern pattern = Pattern.compile( "(reTestVersion=\"(.*?)\")" );
		final Matcher matcher = pattern.matcher( actual );
		if ( !matcher.find() ) {
			return actual;
		}
		final String version = matcher.group( 2 );
		if ( version.equals( UNSPECIFIED_DEVELOPMENT_VERSION ) ) {
			return actual;
		}
		final String versionString = matcher.group( 1 );
		return actual.replace( versionString, "reTestVersion=\"" + UNSPECIFIED_DEVELOPMENT_VERSION + "\"" );
	}

	private static class MavenConformJUnitStackTraceNamer implements ApprovalNamer {

		private final StackTraceReflectionResult info;

		public MavenConformJUnitStackTraceNamer() {
			info = TestUtils.getCurrentFileForMethod( new JUnitStackSelector() );
		}

		@Override
		public String getApprovalName() {
			return String.format( "%s.%s", info.getClassName(), info.getMethodName() );
		}

		@Override
		public String getSourceFilePath() {
			return (info.getSourceFile().getAbsolutePath() + File.separator).replace(
					File.separator + "src" + File.separator + "test" + File.separator + "java",
					File.separator + "src" + File.separator + "test" + File.separator + "resources" );
		}
	}

	public static class JUnitStackSelector implements StackElementSelector {

		@Override
		public StackTraceElement selectElement( final StackTraceElement[] trace ) throws Exception {
			return TestCaseFinder.getInstance().findTestCaseMethodInStack( trace ).getStackTraceElement();
		}

		@Override
		public void increment() {}
	}

	public static class ApprovalAutoApprover implements ApprovalFailureReporter {

		public static final ApprovalFailureReporter INSTANCE = new ApprovalAutoApprover();

		@Override
		public void report( final String received, final String approved ) {
			try {
				Files.copy( Paths.get( received ), Paths.get( approved ) );
			} catch ( final IOException e ) {
				throw new UncheckedIOException( e );
			}
		}
	}
}
