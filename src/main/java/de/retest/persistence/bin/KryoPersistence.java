package de.retest.persistence.bin;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.TreeMultiset;

import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.retest.persistence.Persistable;
import de.retest.persistence.Persistence;

public class KryoPersistence<T extends Persistable> implements Persistence<T> {

	private final Kryo kryo = createKryo();

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
		try ( final Output output = new Output( Files.newOutputStream( Paths.get( identifier ) ) ) ) {
			kryo.writeClassAndObject( output, element );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T load( final URI identifier ) throws IOException {
		try ( final Input input = new Input( Files.newInputStream( Paths.get( identifier ) ) ) ) {
			return (T) kryo.readClassAndObject( input );
		}
	}

}
