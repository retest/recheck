package de.retest.recheck.persistence.bin;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Registration;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.objenesis.strategy.StdInstantiatorStrategy;
import com.google.common.collect.TreeMultiset;

import de.retest.recheck.persistence.IncompatibleReportVersionException;
import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.ui.review.GoldenMasterSource;
import de.retest.recheck.util.FileUtil;
import de.retest.recheck.util.VersionProvider;
import lombok.extern.slf4j.Slf4j;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;

@Slf4j
public class KryoPersistence<T extends Persistable> implements Persistence<T> {

	private static final String OLD_RECHECK_VERSION = "an old recheck version (pre 1.5.0)";

	private static final Map<Class<?>, Integer> compatibleVersions = createCompatibleVersions();

	private static Map<Class<?>, Integer> createCompatibleVersions() {
		final Map<Class<?>, Integer> map = new HashMap<>();

		// Specify the class and lowest readable version here
		map.put( TestReport.class, TestReport.PERSISTENCE_VERSION );

		return map;
	}

	private final Kryo kryo;
	private final String version;

	public KryoPersistence() {
		this( createKryo(), VersionProvider.RECHECK_VERSION );
	}

	/**
	 * Testing only!
	 *
	 * @param kryo
	 *            {@code Kryo} instance to use.
	 * @param version
	 *            recheck version to use.
	 */
	KryoPersistence( final Kryo kryo, final String version ) {
		this.kryo = kryo;
		this.version = version;
	}

	private static Kryo createKryo() {
		final Kryo kryo = new Kryo();
		kryo.setInstantiatorStrategy( new StdInstantiatorStrategy() );
		kryo.setReferences( true );

		kryo.setRegistrationRequired( false ); // TODO remove, see #836

		kryo.register( TestReport.class );
		kryo.register( GoldenMasterSource.class );
		kryo.register( SuiteReplayResult.class );
		kryo.register( ArrayList.class );
		// TODO add more, see #836

		final Registration registration = kryo.register( TreeMultiset.class );
		registration.setInstantiator( TreeMultiset::create );

		return kryo;
	}

	@Override
	public void save( final URI identifier, final T element ) throws IOException {
		final Path path = Paths.get( identifier );
		final File file = path.toFile();
		FileUtil.ensureFolder( path.toFile() );
		try {
			log.debug( "Writing {} to {}. Do not write to same identifier or interrupt until done.", element,
					identifier );
			save( newOutputStream( path ), element );
			log.debug( "Done writing {} to {}", element, identifier );
		} catch ( final Throwable t ) {
			log.error( "Error writing to file '{}'. Deleting what has been written to not leave corrupt file behind...",
					identifier, t );
			FileUtils.deleteQuietly( file );
			throw t;
		}
	}

	public void save( final OutputStream outputStream, final T element ) throws IOException {
		try ( Output output = new Output( new LZ4FrameOutputStream( outputStream ) ) ) {
			output.writeString( version );
			kryo.writeClassAndObject( output, element );
		}
	}

	@Override
	public T load( final URI identifier ) throws IOException {
		final Path path = Paths.get( identifier );
		return load( newInputStream( path ), identifier );
	}

	@SuppressWarnings( "unchecked" )
	public T load( final InputStream in, final URI identifier ) throws IOException {
		String writerVersion = null;
		try ( Input input = new Input( new LZ4FrameInputStream( in ) ) ) {
			writerVersion = input.readString();
			final T persistable = (T) kryo.readClassAndObject( input );
			if ( !isCompatible( persistable ) ) {
				throw new IncompatibleReportVersionException( writerVersion, version, identifier );
			}
			return persistable;
		} catch ( final IncompatibleReportVersionException | NoSuchFileException e ) {
			throw e;
		} catch ( final Exception e ) {
			if ( version.equals( writerVersion ) ) {
				throw e;
			}
			if ( !isKnownFormat( writerVersion ) ) {
				writerVersion = OLD_RECHECK_VERSION;
			}
			throw new IncompatibleReportVersionException( writerVersion, version, identifier, e );
		}
	}

	private boolean isCompatible( final T persistable ) {
		return isCompatible( persistable.getClass(), persistable.version() );
	}

	// For testing only
	boolean isCompatible( final Class<? extends Persistable> clazz, final int version ) {
		final Integer minVersion = compatibleVersions.get( clazz );
		return minVersion == null || minVersion <= version;
	}

	private static final Pattern VERSION_CHARS = Pattern.compile( "[\\w\\.\\{\\}\\$\\-]+" );

	protected static boolean isKnownFormat( final String writerVersion ) {
		if ( writerVersion == null ) {
			return false;
		}
		final Matcher m = VERSION_CHARS.matcher( writerVersion );
		return m.matches();
	}

}
