package de.retest.recheck.persistence;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import de.retest.recheck.Properties;
import de.retest.recheck.auth.RetestAuthentication;
import de.retest.recheck.persistence.bin.KryoPersistence;
import de.retest.recheck.persistence.xml.XmlFolderPersistence;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloudPersistence<T extends Persistable> implements Persistence<T> {
	private static final String SERVICE_ENDPOINT = "https://marvin.prod.cloud.retest.org/api/report";
	private final KryoPersistence<T> kryoPersistence = new KryoPersistence<>();
	private final XmlFolderPersistence<T> folderPersistence = new XmlFolderPersistence<>( getXmlTransformer() );

	public static final String RECHECK_API_KEY = "RECHECK_API_KEY";

	@Override
	public void save( final URI identifier, final T element ) throws IOException {
		kryoPersistence.save( identifier, element );

		if ( isAggregatedReport( identifier ) && element instanceof TestReport ) {
			saveToCloud( identifier, (TestReport) element );
		}
	}

	private boolean isAggregatedReport( final URI identifier ) {
		return identifier.getPath().endsWith( Properties.AGGREGATED_TEST_REPORT_FILE_NAME );
		}

	private List<String> getTestClasses( final TestReport report ) {
		return report.getSuiteReplayResults().stream() //
				.map( SuiteReplayResult::getName ) //
				.collect( Collectors.toList() );
	}

	private void saveToCloud( final URI identifier, final TestReport report ) throws IOException {
		final HttpResponse<String> uploadUrlResponse = getUploadUrl();

		final ReportUploadMetadata metadata = ReportUploadMetadata.builder() //
				.location( identifier ) //
				.uploadUrl( uploadUrlResponse.getBody() ) //
				.testClasses( getTestClasses( report ) ) //
				.build();

		if ( uploadUrlResponse.isSuccess() ) {
			uploadReport( metadata );
		}
	}

	private void uploadReport( final ReportUploadMetadata metadata ) throws IOException {
		final String reportName = metadata.getTestClasses() //
				.stream() //
				.collect( Collectors.joining( ", " ) );

		final HttpResponse<?> uploadResponse = Unirest.put( metadata.getUploadUrl() ) //
				.header( "x-amz-meta-report-name", reportName ) //
				.field( "upload", new File( metadata.getLocation() ) ) //
				.asEmpty();

		if ( uploadResponse.isSuccess() ) {
			log.info( "Successfully uploaded report to rehub" );
		}
	}

	private HttpResponse<String> getUploadUrl() {
		final RetestAuthentication auth = RetestAuthentication.getInstance();
		final String token = String.format( "Bearer %s", auth.getAccessTokenString() );

		return Unirest.post( SERVICE_ENDPOINT ) //
				.header( "Authorization", token )//
				.asString();
	}

	@Override
	public T load( final URI identifier ) throws IOException {
		if ( Paths.get( identifier ).toFile().isDirectory() ) {
			return folderPersistence.load( identifier );
		}
		return kryoPersistence.load( identifier );
	}

}
