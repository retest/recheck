package de.retest.recheck.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Path should be persisted as String, use {@link PathAdapter}.
 */
public class Path implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PATH_SEPARATOR = "/";

	private final Path parentPath;

	private final PathElement element;

	private String toStringCache;

	private Path() {
		// for JAXB
		parentPath = null;
		element = null;
	}

	private Path( final PathElement element ) {
		this( null, element );
	}

	private Path( final Path parentPath, final PathElement element ) {
		this.parentPath = parentPath;
		this.element = element;
	}

	public Path getParentPath() {
		return parentPath;
	}

	public PathElement getElement() {
		return element;
	}

	@Override
	public String toString() {
		if ( toStringCache == null ) {
			final String parentPathTemp = parentPath == null ? "" : parentPath.toString() + PATH_SEPARATOR;
			toStringCache = parentPathTemp + (element == null ? "" : element.toString());
		}
		return toStringCache;
	}

	@Override
	public int hashCode() {
		return Objects.hash( element, parentPath );
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null || getClass() != obj.getClass() ) {
			return false;
		}
		final Path other = (Path) obj;
		if ( !Objects.equals( element, other.element ) || !Objects.equals( parentPath, other.parentPath ) ) {
			return false;
		}
		return true;
	}

	public boolean isParent( final Path path ) {
		return isParent( path.toString() );
	}

	public boolean isParent( final String path ) {
		return path.startsWith( toString() );
	}

	public static Path fromString( String path ) {
		while ( path.startsWith( PATH_SEPARATOR ) ) {
			path = path.substring( 1 );
		}
		if ( !path.contains( PATH_SEPARATOR ) ) {
			return Path.path( PathElement.fromString( path ) );
		}
		Path result = null;
		while ( path.contains( PATH_SEPARATOR ) ) {
			final String pathElement = path.substring( 0, path.indexOf( PATH_SEPARATOR ) );
			result = Path.path( result, PathElement.fromString( pathElement ) );
			path = path.substring( path.indexOf( PATH_SEPARATOR ) + 1 );
		}
		if ( path.isEmpty() ) {
			return result;
		}
		return Path.path( result, PathElement.fromString( path ) );
	}

	// Ensures that each path exists exactly once.
	private static final Map<String, Path> paths = new HashMap<>();

	public static Path path( final PathElement element ) {
		Path result = paths.get( element.toString() );
		if ( result == null ) {
			result = new Path( element );
			paths.put( element.toString(), result );
		}
		return result;
	}

	public static Path path( final Path parentPath, final PathElement element ) {
		final Path temp = new Path( parentPath, element );
		Path result = paths.get( temp.toString() );
		if ( result == null ) {
			result = temp;
			paths.put( temp.toString(), result );
		}
		return result;
	}
}
