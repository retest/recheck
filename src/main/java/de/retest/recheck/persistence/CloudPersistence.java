package de.retest.recheck.persistence;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import de.retest.recheck.Properties;
import de.retest.recheck.auth.RetestAuthentication;
import de.retest.recheck.persistence.bin.KryoPersistence;
import de.retest.recheck.persistence.xml.XmlFolderPersistence;
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
		if ( identifier.getPath().endsWith( Properties.AGGREGATED_TEST_REPORT_FILE_NAME ) ) {
			saveToCloud( identifier );
		}
	}

	private void saveToCloud( final URI identifier ) throws IOException {
		final HttpResponse<String> uploadUrlResponse = getUploadUrl();

		if ( uploadUrlResponse.isSuccess() ) {
			uploadReport( identifier, uploadUrlResponse.getBody() );
		}
	}

	private void uploadReport( final URI identifier, final String url ) throws IOException {
		final HttpResponse<?> uploadResponse = Unirest.put( url ) //
				.header( "x-amz-meta-report-name", Paths.get( identifier ).getFileName().toString() )
				.field( "upload", new File( identifier ) ) //
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
