package de.retest.recheck.persistence.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.persistence.internal.oxm.record.namespaces.MapNamespacePrefixMapper;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.logging.AbstractSessionLog;

import com.google.common.collect.ImmutableMap;

import de.retest.recheck.persistence.xml.util.SessionLogDelegate;
import de.retest.recheck.persistence.xml.util.StdXmlClassesProvider;
import de.retest.recheck.persistence.xml.util.XmlUtil;

public class XmlTransformer {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( XmlTransformer.class );

	private static final ImmutableMap<String, String> NAMESPACE_MAPPINGS = ImmutableMap
			.of( "http://www.w3.org/2001/XMLSchema", "xsd", "http://www.w3.org/2001/XMLSchema-instance", "xsi" );

	public static enum XmlTransformerConfig {
		USE_LIGHTWEIGHT_XML,
		CREATE_ONLY_FRAGMENT,
	}

	private final XmlTransformerConfig[] config;
	private final Class<?>[] additionalClazzes;

	public XmlTransformer( final Class<?>[] additionalClazzes, final XmlTransformerConfig... config ) {
		this.additionalClazzes = additionalClazzes;
		this.config = config;
	}

	public XmlTransformer( final XmlTransformerConfig config, final Class<?>... additionalClazzes ) {
		this( additionalClazzes, config );
	}

	public XmlTransformer( final Class<?>... additionalClazzes ) {
		this( additionalClazzes, new XmlTransformerConfig[0] );
	}

	public XmlTransformer( final XmlTransformerConfig config, final Set<Class<?>> xmlDataClasses ) {
		this( config, xmlDataClasses.toArray( new Class<?>[xmlDataClasses.size()] ) );
	}

	public XmlTransformer( final Set<Class<?>> xmlDataClasses ) {
		this( null, xmlDataClasses );
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

	// TODO Try if this is possible with a marshaller delegate instead?
	private static final Set<Marshaller> lightweightMarshallerSet = new HashSet<>();

	public static boolean isLightweightMarshaller( final Marshaller m ) {
		return lightweightMarshallerSet.contains( m );
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

			if ( ArrayUtils.contains( config, XmlTransformerConfig.CREATE_ONLY_FRAGMENT ) ) {
				logger.info( "Create only fragment for '{}'.", obj );
				marshaller.setProperty( Marshaller.JAXB_FRAGMENT, true );
			}

			if ( ArrayUtils.contains( config, XmlTransformerConfig.USE_LIGHTWEIGHT_XML ) ) {
				logger.info( "Use lightweight xml for '{}'.", obj );
				lightweightMarshallerSet.add( marshaller );
				XmlUtil.addLightWeightAdapter( marshaller );
			}

			marshaller.marshal( obj, out );

			if ( sessionLog.containsMessages() ) {
				throw new RuntimeException( "Error persisting xml: " + sessionLog.getLog() );
			}
		} catch ( final JAXBException e ) {
			throw new RuntimeException( e );
		} finally {
			if ( ArrayUtils.contains( config, XmlTransformerConfig.USE_LIGHTWEIGHT_XML ) && marshaller != null ) {
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
