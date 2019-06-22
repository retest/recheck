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
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class CloudPersistence<T extends Persistable> implements Persistence<T> {
	private static final String SERVICE_ENDPOINT = "https://marvin.prod.cloud.retest.org/api/report";
	private final KryoPersistence<T> kryoPersistence = new KryoPersistence<>();
	private final XmlFolderPersistence<T> folderPersistence = new XmlFolderPersistence<>( getXmlTransformer() );
	private final OkHttpClient client = new OkHttpClient();

	public static final String RECHECK_API_KEY = "RECHECK_API_KEY";

	@Override
	public void save( final URI identifier, final T element ) throws IOException {
		kryoPersistence.save( identifier, element );
		if ( identifier.getPath().endsWith( Properties.AGGREGATED_TEST_REPORT_FILE_NAME ) ) {
			saveToCloud( identifier );
		}
	}

	private void saveToCloud( final URI identifier ) throws IOException {
		final Response uploadUrlResponse = getUploadUrl();

		if ( uploadUrlResponse.isSuccessful() ) {
			uploadReport( identifier, uploadUrlResponse.body().string() );
		}
	}

	private void uploadReport( final URI identifier, final String url ) throws IOException {
		final Request uploadRequest = new Request.Builder() //
				.url( url ) //
				.put( RequestBody.create( null, new File( identifier ) ) ) //
				.addHeader( "x-amz-meta-report-name", Paths.get( identifier ).getFileName().toString() ) //
				.build();

		final Response uploadResponse = client.newCall( uploadRequest ).execute();

		if ( uploadResponse.isSuccessful() ) {
			log.info( "Sucessfully uploaded report to rehub" );
		}
	}

	private Response getUploadUrl() throws IOException {
		final RetestAuthentication auth = RetestAuthentication.getInstance();
		final String token = String.format( "Bearer %s", auth.getAccessTokenString() );

		final Request uploadUrlRequest = new Request.Builder() //
				.url( SERVICE_ENDPOINT ) //
				.post( RequestBody.create( null, "" ) ) //
				.addHeader( "Authorization", token ) //
				.build();

		return client.newCall( uploadUrlRequest ).execute();
	}

	@Override
	public T load( final URI identifier ) throws IOException {
		if ( Paths.get( identifier ).toFile().isDirectory() ) {
			return folderPersistence.load( identifier );
		}
		return kryoPersistence.load( identifier );
	}

}
