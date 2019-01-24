package de.retest.ui.descriptors;

import static de.retest.util.ObjectUtil.checkNull;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import de.retest.ui.Path;
import de.retest.util.StringSimilarity;

@XmlRootElement
public class PathAttribute extends ParameterizedAttribute {

	public static final ParameterType parameterTypePath = new ParameterType( "PATH" ) {
		@Override
		public Object parse( final String value ) throws ParameterParseException {
			try {
				return Path.fromString( value );
			} catch ( final Exception e ) {
				throw new ParameterParseException( "Value must be a valid and parsable path.", e );
			}
		}
	};

	private static final long serialVersionUID = 1L;

	public static final String PATH_KEY = "path";

	@XmlValue
	private final String path;

	private transient Path cachedPath;

	// Used by JaxB
	protected PathAttribute() {
		path = null;
	}

	public PathAttribute( final Path path ) {
		this( path, null );
	}

	public PathAttribute( final Path path, final String variableName ) {
		super( PATH_KEY, variableName );
		checkNull( path, PATH_KEY );
		cachedPath = path;
		this.path = path.toString();
	}

	@Override
	public Path getValue() {
		if ( cachedPath == null ) {
			cachedPath = Path.fromString( path );
		}
		return cachedPath;
	}

	@Override
	public double match( final Attribute other ) {
		if ( !(other instanceof PathAttribute) ) {
			return NO_MATCH;
		}
		if ( !other.getKey().equals( PATH_KEY ) ) {
			return NO_MATCH;
		}
		final String parentPath = getValue().getParentPath() == null ? "" : getValue().getParentPath().toString();
		final Path otherPath = ((PathAttribute) other).getValue();
		final String otherParentPath = otherPath.getParentPath() == null ? "" : otherPath.getParentPath().toString();
		return StringSimilarity.pathSimilarity( parentPath, otherParentPath );
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		if ( actual instanceof String ) {
			return new PathAttribute( Path.fromString( (String) actual ), getVariableName() );
		}
		return new PathAttribute( (Path) actual, getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new PathAttribute( getValue(), variableName );
	}

	@Override
	public ParameterType getType() {
		return parameterTypePath;
	}
}
