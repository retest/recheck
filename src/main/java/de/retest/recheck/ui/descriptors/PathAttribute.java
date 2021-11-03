package de.retest.recheck.ui.descriptors;

import static de.retest.recheck.util.ObjectUtil.checkNull;

import java.io.Serializable;

import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.PathAdapter;
import de.retest.recheck.util.StringSimilarity;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
	@XmlValueExtension
	@XmlJavaTypeAdapter( PathAdapter.class )
	private final Path path;

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
		this.path = path;
	}

	@Override
	public Path getValue() {
		return path;
	}

	@Override
	public double match( final Attribute other ) {
		if ( !(other instanceof PathAttribute) ) {
			return NO_MATCH;
		}
		if ( !PATH_KEY.equals( other.getKey() ) ) {
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
