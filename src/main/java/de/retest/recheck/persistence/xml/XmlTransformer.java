package de.retest.recheck.persistence.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.persistence.internal.oxm.record.namespaces.MapNamespacePrefixMapper;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.logging.AbstractSessionLog;

import com.google.common.collect.ImmutableMap;

import de.retest.recheck.persistence.xml.util.SessionLogDelegate;
import de.retest.recheck.persistence.xml.util.StdXmlClassesProvider;
import de.retest.recheck.persistence.xml.util.XmlUtil;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlTransformer {

	private static final ImmutableMap<String, String> NAMESPACE_MAPPINGS = ImmutableMap
			.of( "http://www.w3.org/2001/XMLSchema", "xsd", "http://www.w3.org/2001/XMLSchema-instance", "xsi" );

	// TODO Try if this is possible with a marshaller delegate instead?
	private static final Set<Marshaller> lightweightMarshallerSet = new HashSet<>();

	public static boolean isLightweightMarshaller( final Marshaller m ) {
		return lightweightMarshallerSet.contains( m );
	}

	private final XmlTransformerConfiguration config;
	private final Class<?>[] additionalClazzes;

	public XmlTransformer( final XmlTransformerConfiguration config, final Class<?>... additionalClazzes ) {
		this.config = config;
		this.additionalClazzes = additionalClazzes;
	}

	public XmlTransformer( final Class<?>... additionalClazzes ) {
		this( XmlTransformerConfiguration.builder().build(), additionalClazzes );
	}

	public XmlTransformer( final Set<Class<?>> xmlDataClasses ) {
		this( xmlDataClasses.toArray( new Class<?>[xmlDataClasses.size()] ) );
	}

	public <T> T fromXML( final InputStream in ) {
		return fromXML( in, null );
	}

	public <T> T fromXML( final InputStream in, final Unmarshaller.Listener listener ) {
		try {
			final JAXBContext jc = createJAXBContext( additionalClazzes );
			final Unmarshaller unmarshaller = jc.createUnmarshaller();
			unmarshaller.setEventHandler( new DefaultValidationEventHandler() );
			unmarshaller.setListener( listener );

			@SuppressWarnings( "unchecked" )
			final T result = (T) unmarshaller.unmarshal( in );
			return result;

		} catch ( final JAXBException e ) {
			throw new RuntimeException( e );
		}
	}

	public void toXML( final Object obj, final OutputStream out, final Marshaller.Listener listener ) {
		Marshaller marshaller = null;
		try {
			final JAXBContext jc = createJAXBContext( additionalClazzes );
			marshaller = jc.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
			marshaller.setProperty( MarshallerProperties.NAMESPACE_PREFIX_MAPPER,
					new MapNamespacePrefixMapper( NAMESPACE_MAPPINGS ) );
			marshaller.setProperty( MarshallerProperties.INDENT_STRING, "\t" );
			marshaller.setEventHandler( new DefaultValidationEventHandler() );
			marshaller.setListener( listener );
			final SessionLogDelegate sessionLog = new SessionLogDelegate( AbstractSessionLog.getLog() );
			AbstractSessionLog.setLog( sessionLog );

			if ( config.isOnlyFragment() ) {
				log.info( "Create only fragment for '{}'.", obj );
				marshaller.setProperty( Marshaller.JAXB_FRAGMENT, true );
			}

			if ( config.isLightweightXml() ) {
				log.info( "Use lightweight XML for '{}'.", obj );
				lightweightMarshallerSet.add( marshaller );
				XmlUtil.addLightWeightAdapter( marshaller );
			}

			marshaller.marshal( obj, out );

			if ( sessionLog.containsMessages() ) {
				throw new RuntimeException( "Error persisting XML: " + sessionLog.getLog() );
			}
		} catch ( final JAXBException e ) {
			throw new RuntimeException( e );
		} finally {
			if ( config.isLightweightXml() && marshaller != null ) {
				lightweightMarshallerSet.remove( marshaller );
			}
		}
	}

	public void toXML( final Object obj, final OutputStream out ) {
		toXML( obj, out, null );
	}

	public String toXML( final Object obj ) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream( baos );

		toXML( obj, ps );

		return baos.toString();
	}

	/**
	 * Use a well-defined JAXB class context, so we don't interfere with some SUT-configured context. Although a context
	 * <a href=https://stackoverflow.com/a/7400735>"should only be created once and reused"</a>, this fails the
	 * corresponding test for this class.
	 *
	 * @param additionalClazzes
	 *            Classes to be bound.
	 * @return A sparkling fresh {@code JAXBContext}.
	 */
	private JAXBContext createJAXBContext( final Class<?>... additionalClazzes ) {
		try {
			final Class<?>[] contextClasses = StdXmlClassesProvider.getXmlDataClasses( additionalClazzes );
			return JAXBContextFactory.createContext( contextClasses, Collections.emptyMap() );
		} catch ( final JAXBException e ) {
			throw new RuntimeException( e );
		}
	}
}
