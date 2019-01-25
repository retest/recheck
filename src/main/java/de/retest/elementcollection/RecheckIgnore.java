package de.retest.elementcollection;

import static de.retest.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.retest.ExecutingTestContext;
import de.retest.Properties;
import de.retest.persistence.Persistence;
import de.retest.ui.IgnoredTypes;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.RootElement;

public class RecheckIgnore {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( RecheckIgnore.class );

	public static final String IGNORED_ATTRIBUTES_PROPERTY = "de.retest.ignoredAttributes";
	private static final List<String> ignoredGlobal =
			Arrays.asList( System.getProperty( IGNORED_ATTRIBUTES_PROPERTY, "" ).split( Properties.VALUES_SEPARATOR ) );

	private static RecheckIgnore instance;

	private final IgnoredTypes ignoredTypes;

	private ElementCollection ignored;
	private boolean dirty = false;

	public static void loadIgnored( final Persistence<ElementCollection> persistence,
			final IgnoredTypes ignoredTypes ) {
		if ( instance == null ) {
			instance = new RecheckIgnore( loadRecheckIgnore( persistence ), ignoredTypes );
		}
	}

	public RecheckIgnore( final ElementCollection ignored, final IgnoredTypes ignoredTypes ) {
		this.ignored = ignored;
		this.ignoredTypes = ignoredTypes;
	}

	public static RecheckIgnore getInstance() {
		if ( instance == null ) {
			throw new UninitializedRecheckIgnoreException( "recheck ignore must be loaded before being used." );
		}
		return instance;
	}

	private static ElementCollection loadRecheckIgnore( final Persistence<ElementCollection> persistence ) {
		final File ignoredFile = ElementCollectionFileUtils.getIgnoredDiffsFile();
		if ( !ignoredFile.exists() ) {
			logger.info( "No recheck ignore file '{}' found, be aware we are looking relative to the work directory.",
					canonicalPathQuietly( ignoredFile ) );
			return new ElementCollection();
		}
		logger.info( "Loading recheck ignore from file '{}'.", canonicalPathQuietly( ignoredFile ) );
		try {
			final ElementCollection load = persistence.load( ignoredFile.toURI() );
			return load != null ? load : new ElementCollection();
		} catch ( final IllegalArgumentException e ) {
			logger.error( "Error loading recheck ignore from file '{}'.", canonicalPathQuietly( ignoredFile ), e );
			return new ElementCollection();
		} catch ( final IOException e ) {
			logger.error( "Error loading recheck ignore from file '{}'.", canonicalPathQuietly( ignoredFile ), e );
			return new ElementCollection();
		}
	}

	public void reloadRecheckIgnore( final Persistence<ElementCollection> persistence ) {
		logger.info( "Reloading recheck ignore." );
		ignored = loadRecheckIgnore( persistence );
	}

	public void saveRecheckIgnore( final Persistence<ElementCollection> persistence ) {
		final File ignoredFile = ElementCollectionFileUtils.getIgnoredDiffsFile();
		logger.info( "Saving recheck ignore to file '{}'.", canonicalPathQuietly( ignoredFile ) );
		try {
			persistence.save( ignoredFile.toURI(), ignored );
		} catch ( final IOException e ) {
			throw new RuntimeException(
					"Could not save recheck ignore to file '" + canonicalPathQuietly( ignoredFile ) + "'.", e );
		}
		dirty = false;
	}

	public List<RootElement> filter( final List<RootElement> input ) {
		final List<RootElement> result = new ArrayList<>();
		for ( final RootElement rootElement : input ) {
			final int componentCnt = rootElement.countAllContainedElements();
			filter( rootElement );
			logger.debug( "Filtering with recheck ignore removed {} components from window.",
					componentCnt - rootElement.countAllContainedElements() );
			result.add( ignored.filterAttributes( rootElement ) );
		}
		return result;
	}

	private void filter( final Element element ) {
		final List<Element> children = element.getContainedElements();
		final Iterator<Element> iterator = children.iterator();
		while ( iterator.hasNext() ) {
			final Element childElement = iterator.next();
			if ( ignored.contains( childElement ) ) {
				logger.debug( "Removing {} because it is ignored.", childElement );
				iterator.remove();
			}
			filter( childElement );
		}
	}

	public List<RootElement> getFilteredTargetableRootElements( final ExecutingTestContext context ) {
		final List<RootElement> targetableRootElements = context.getEnvironment().getTargetableRootElements();
		final List<RootElement> result = filter( targetableRootElements );
		if ( countAllComponents( result ) < countAllComponents( targetableRootElements ) * 0.5 ) {
			logger.warn( "We are removing more than 50% of the child components from the state. "
					+ "This very likely leads to confounding of states! Bad choice for an ignored/excluded element?" );
		}
		return result;
	}

	private double countAllComponents( final List<RootElement> targetableRootElements ) {
		int result = 0;
		for ( final RootElement rootElement : targetableRootElements ) {
			result += rootElement.countAllContainedElements();
		}
		return result;
	}

	public void ignoreElement( final Element element ) {
		ignored.add( element );
		dirty = true;
	}

	public void unignoreElement( final IdentifyingAttributes identifyingAttributes ) {
		ignored.remove( identifyingAttributes );
		dirty = true;
	}

	public boolean shouldIgnoreElement( final Element element ) {
		if ( element == null ) {
			return false;
		}
		return ignored.contains( element ) || ignoredTypes.contains( element );
	}

	public boolean shouldIgnoreElement( final IdentifyingAttributes identifyingAttributes ) {
		if ( identifyingAttributes == null ) {
			return false;
		}
		return ignored.contains( identifyingAttributes ) || ignoredTypes.contains( identifyingAttributes );
	}

	public boolean wasChangedButNotPersisted() {
		return dirty;
	}

	public ElementCollection getIgnored() {
		return ignored;
	}

	public boolean shouldIgnoreAttribute( final IdentifyingAttributes comp, final String key ) {
		if ( ignoredGlobal.contains( key ) ) {
			return true;
		}
		return ignored.containsAttribute( comp, key );
	}

	public boolean shouldIgnoreAttribute( final String elementRetestId, final String key ) {
		if ( ignoredGlobal.contains( key ) ) {
			return true;
		}
		return ignored.containsAttribute( elementRetestId, key );
	}

	public void ignoreAttribute( final Element element, final String key ) {
		ignored.addAttribute( element, key );
		dirty = true;
	}

	public void unignoreAttribute( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final String key ) {
		ignored.remove( retestId, identifyingAttributes, key );
		dirty = true;
	}

	public Set<String> getIgnoredAttributes( final Element component ) {
		return ignored.getAttributes( component );
	}

	/**
	 * Testing only!
	 *
	 * @param elementCollection
	 *            element collection to be used
	 * @return instance with given element collection
	 */
	public static RecheckIgnore getTestInstance( final ElementCollection elementCollection ) {
		logger.warn( "Testing only! Creating recheck ignore from given element collection." );
		instance = new RecheckIgnore( elementCollection );
		return instance;
	}

	public static RecheckIgnore getTestInstance() {
		return getTestInstance( new ElementCollection() );
	}

	private RecheckIgnore( final ElementCollection elementCollection ) {
		ignoredTypes = new IgnoredTypes.NoIgnoredTypes();
		ignored = elementCollection;
	}
}
