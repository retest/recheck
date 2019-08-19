package de.retest.recheck.persistence.bin;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.TreeMultiset;

import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.retest.recheck.persistence.IncompatibleReportVersionException;
import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.util.FileUtil;
import de.retest.recheck.util.VersionProvider;

public class KryoPersistence<T extends Persistable> implements Persistence<T> {

	private final Kryo kryo = createKryo();
	private final String version = VersionProvider.RETEST_VERSION;

	private Kryo createKryo() {
		final Kryo kryo = new Kryo();

		kryo.setInstantiatorStrategy( new Kryo.DefaultInstantiatorStrategy( new StdInstantiatorStrategy() ) );

		final Registration registration = kryo.getRegistration( TreeMultiset.class );
		registration.setInstantiator( TreeMultiset::create );

		UnmodifiableCollectionsSerializer.registerSerializers( kryo );

		return kryo;
	}

	@Override
	public void save( final URI identifier, final T element ) throws IOException {
		final Path path = Paths.get( identifier );
		FileUtil.ensureFolder( path.toFile() );
		try ( final Output output = new Output( Files.newOutputStream( path ) ) ) {
			output.writeString( version );
			kryo.writeClassAndObject( output, element );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T load( final URI identifier ) throws IOException {
		String writerVersion = "unknown";
		try ( final Input input = new Input( Files.newInputStream( Paths.get( identifier ) ) ) ) {
			writerVersion = input.readString();
			return (T) kryo.readClassAndObject( input );
		} catch ( final KryoException e ) {
			throw new IncompatibleReportVersionException( writerVersion, version, identifier, e );
		}
	}

}
