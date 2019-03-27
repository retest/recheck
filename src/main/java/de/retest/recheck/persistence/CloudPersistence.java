package de.retest.recheck.persistence;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import org.keycloak.adapters.ServerRequest.HttpFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.auth.Authentication;
import de.retest.recheck.persistence.bin.KryoPersistence;
import de.retest.recheck.persistence.xml.XmlFolderPersistence;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudPersistence<T extends Persistable> implements Persistence<T> {
	private static final String SERVICE_ENDPOINT = "https://marvin.dev.cloud.retest.org/report";
	private static final Logger logger = LoggerFactory.getLogger( CloudPersistence.class );
	private final KryoPersistence<T> kryoPersistence = new KryoPersistence<>();
	private final XmlFolderPersistence<T> folderPersistence = new XmlFolderPersistence<>( getXmlTransformer() );
	private final OkHttpClient client = new OkHttpClient();

	public static final String RECHECK_API_KEY = "RECHECK_API_KEY";

	@Override
	public void save( final URI identifier, final T element ) throws IOException {
		kryoPersistence.save( identifier, element );
		try {
			saveToCloud( identifier );
		} catch ( final HttpFailure e ) {
			logger.error( "Error", e );
		}
	}

	private void saveToCloud( final URI identifier ) throws IOException, HttpFailure {
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
			logger.info( "Sucessfully uploaded report to '{}'.", url.split( "\\?" )[0] );
		}
	}

	private Response getUploadUrl() throws IOException, HttpFailure {
		final String offlineRefreshToken = System.getenv().get( RECHECK_API_KEY );
		final Authentication auth = Authentication.getInstance( offlineRefreshToken );
		final String token = String.format( "Bearer %s", auth.exchangeToken() );

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
