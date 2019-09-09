package de.retest.recheck.util;

import static java.io.File.separator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.approvaltests.Approvals;
import org.approvaltests.core.ApprovalFailureReporter;
import org.approvaltests.namer.ApprovalNamer;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.writers.ApprovalTextWriter;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import com.spun.util.io.StackElementSelector;
import com.spun.util.tests.StackTraceReflectionResult;
import com.spun.util.tests.TestUtils;

import de.retest.recheck.TestCaseFinder;
import lombok.RequiredArgsConstructor;

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

	public static void verifyMultiXsd( final Class<?> clazz ) throws JAXBException, IOException {
		verifyMulti( generateSchema( clazz ), clazz.getSimpleName(), "xsd" );
	}

	private static void verifyMulti( final String actual, final String name, final String fileExtensionWithoutDot ) {
		Approvals.verify( new ApprovalTextWriter( actual, fileExtensionWithoutDot ),
				new MultiMavenConformJUnitStackTraceNamer( name ), DIFF_HANDLER );
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

	private static String generateSchema( final Class<?> type ) throws JAXBException, IOException {
		final StringWriter stringWriter = new StringWriter();

		final JAXBContext jaxbContext =
				JAXBContextFactory.createContext( new Class<?>[] { type }, Collections.emptyMap() );
		jaxbContext.generateSchema( new SchemaOutputResolver() {
			@Override
			public final Result createOutput( final String namespaceURI, final String suggestedFileName )
					throws IOException {
				final StreamResult result = new StreamResult( stringWriter );
				result.setSystemId( suggestedFileName );
				return result;
			}
		} );

		return stringWriter.toString();
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
			final String pathPrefix = separator + "src" + separator + "test" + separator;
			final String srcTestJavaPath = pathPrefix + "java";
			final String srcTestResourcesPath = pathPrefix + "resources";
			final String testClassPath = info.getSourceFile().getAbsolutePath();
			return (testClassPath + separator).replace( srcTestJavaPath, srcTestResourcesPath );
		}
	}

	@RequiredArgsConstructor
	public static class MultiMavenConformJUnitStackTraceNamer implements ApprovalNamer {

		private final ApprovalNamer approvalNamer = new MavenConformJUnitStackTraceNamer();
		private final String name;

		@Override
		public String getApprovalName() {
			return name;
		}

		@Override
		public String getSourceFilePath() {
			return approvalNamer.getSourceFilePath() + separator + approvalNamer.getApprovalName() + separator;
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
