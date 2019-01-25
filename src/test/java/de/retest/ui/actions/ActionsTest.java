package de.retest.ui.actions;

import static de.retest.ui.descriptors.StringAttribute.parameterTypeBoolean;
import static de.retest.ui.descriptors.StringAttribute.parameterTypeClass;
import static de.retest.ui.descriptors.StringAttribute.parameterTypeInteger;
import static de.retest.ui.descriptors.StringAttribute.parameterTypeString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.retest.ui.Path;
import de.retest.ui.descriptors.AttributeUtil;
import de.retest.ui.descriptors.Attributes;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.OutlineAttribute;
import de.retest.ui.descriptors.ParameterType;
import de.retest.ui.descriptors.ParameterizedAttribute;
import de.retest.ui.descriptors.PathAttribute;
import de.retest.ui.descriptors.VariableNameAttributeDifference;
import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.review.ActionChangeSet;
import de.retest.ui.review.AttributeChanges;

public class ActionsTest {

	@BeforeClass
	public static void setup() {
		ParameterType.registerStdParameterTypes();
	}

	@Test
	public void createParameterValueChangeSet_should_create_proper_change_set() {
		final Element element = mock( Element.class );

		final IdentifyingAttributes attributes = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attributes );

		final ParameterizedAction action = mock( ParameterizedAction.class );
		when( action.getTargetElement() ).thenReturn( element );

		final ParameterizedAttribute attribute = mock( ParameterizedAttribute.class );

		final ActionChangeSet change = Actions.createParameterValueChangeSet( action, attribute, "change" );

		assertThat( change.isEmpty() ).isFalse();
		final AttributeChanges identAttributeChanges = change.getIdentAttributeChanges();
		assertThat( identAttributeChanges.size() ).isEqualTo( 1 );

		final ArrayList<AttributeDifference> differences =
				new ArrayList<>( identAttributeChanges.getAll( attributes ) );
		assertThat( differences ).hasSize( 1 );
		assertThat( differences.get( 0 ) ).isInstanceOf( VariableNameAttributeDifference.class );
		assertThat( differences.get( 0 ).getActual() ).isEqualTo( "change" );
	}

	@Test
	public void convertActionIfPossible_should_fallback_to_default_action() {
		final Action notParameterized = mock( Action.class );
		final ValueProvider provider = new MapValueProvider( Collections.<String, String> emptyMap() );

		final Action notConverted = Actions.convertActionIfPossible( notParameterized, provider );

		assertThat( notConverted ).isEqualTo( notParameterized );
	}

	@Test
	public void convertActionIfPossible_should_convert_and_keep_and_default_paramters_and_attributes() {
		final ParameterizedAction action = createAction();
		final Map<String, String> map = new HashMap<>();
		map.put( "path", "path/to/other/parent" );
		map.put( "int1", "42" );
		map.put( "int2", "4711" );

		final Action convert = Actions.convertActionIfPossible( action, new MapValueProvider( map ) );

		assertThat( convert ).isInstanceOf( ParameterizedAction.class );
		final ParameterizedAction parameterizedAction = (ParameterizedAction) convert;

		final IdentifyingAttributes convertAttributes = convert.getTargetElement().getIdentifyingAttributes();
		assertThat( convertAttributes.getPath() ).isEqualTo( "path[1]/to[1]/other[1]/parent[1]" );
		assertThat( AttributeUtil.getOutline( convertAttributes ) ).isEqualTo( new Rectangle( 0, 0, 0, 0 ) );

		final Set<ActionParameter> actionParameter = parameterizedAction.getActionParameter();
		assertThat( actionParameter ).hasSize( 7 );

		final ActionParameter a1 = new ActionParameter( "p1", "42", parameterTypeInteger, "int1" );
		final ActionParameter a2 = new ActionParameter( "p2", "4711", parameterTypeInteger, "int2" );
		final ActionParameter a3 = new ActionParameter( "p3", "3", parameterTypeString );
		final ActionParameter a4 = new ActionParameter( "p4", "true", parameterTypeBoolean, "bool" );
		final ActionParameter a5 = new ActionParameter( "p5", Boolean.class.getName(), parameterTypeClass );
		assertThat( actionParameter ).contains( a1, a2, a3, a4, a5 );
	}

	@Test
	public void convertActionIfPossible_with_errors_should_convert_and_keep_and_default_paramters_and_attributes() {
		final ParameterizedAction action = createAction();
		final Map<String, String> map = new HashMap<>();
		map.put( "path", "path/to/other/parent" );
		map.put( "int1", "Hi" );
		map.put( "int2", "42" );
		map.put( "bool", "No_Bool" );

		final ValueProvider provider = new MapValueProvider( map );

		final Action convert = Actions.convertActionIfPossible( action, provider );

		assertThat( convert ).isNotNull();
		assertThat( convert ).isNotEqualTo( action );
		assertThat( convert ).isInstanceOf( ParameterizedAction.class );

		final ParameterizedAction parameterizedAction = (ParameterizedAction) convert;

		final IdentifyingAttributes convertAttributes = convert.getTargetElement().getIdentifyingAttributes();
		assertThat( convertAttributes.getAttributes() ).hasSize( 2 );
		assertThat( convertAttributes.getPath() ).isEqualTo( "path[1]/to[1]/other[1]/parent[1]" );
		assertThat( AttributeUtil.getOutline( convertAttributes ) ).isEqualTo( new Rectangle( 0, 0, 0, 0 ) );

		final Set<ActionParameter> actionParameter = parameterizedAction.getActionParameter();
		assertThat( actionParameter ).hasSize( 7 );

		final ActionParameter a1 = new ActionParameter( "p1", "1", parameterTypeInteger, "int1" );
		final ActionParameter a2 = new ActionParameter( "p2", "42", parameterTypeInteger, "int2" );
		final ActionParameter a3 = new ActionParameter( "p3", "3", parameterTypeString );
		final ActionParameter a4 = new ActionParameter( "p4", "true", parameterTypeBoolean, "bool" );
		final ActionParameter a5 = new ActionParameter( "p5", Boolean.class.getName(), parameterTypeClass );
		assertThat( actionParameter ).contains( a1, a2, a3, a4, a5 );
	}

	@Test
	public void convertActionIfPossible_with_empty_should_return_same_action() {
		final ParameterizedAction action = createAction();
		final MapValueProvider provider = new MapValueProvider( Collections.<String, String> emptyMap() );

		final Action convert = Actions.convertActionIfPossible( action, provider );

		assertThat( convert ).isEqualTo( action );
	}

	@Test
	public void validateAction_with_action_should_be_true() {
		final Action action = mock( Action.class );
		final MapValueProvider provider = new MapValueProvider( Collections.<String, String> emptyMap() );

		assertThat( Actions.validateAction( action, provider ) ).isTrue();
	}

	@Test
	public void validateAction_with_action_and_no_provider_should_be_true() {
		final Action action = mock( Action.class );

		assertThat( Actions.validateAction( action, null ) ).isTrue();
	}

	@Test
	public void validateAction_with_parameterized_action_should_be_true() {
		final Action action = createAction();
		final MapValueProvider provider = new MapValueProvider( Collections.<String, String> emptyMap() );

		assertThat( Actions.validateAction( action, provider ) ).isTrue();
	}

	@Test
	public void validateAction_with_parameterized_action_and_no_provider_should_be_true() {
		final Action action = createAction();

		assertThat( Actions.validateAction( action, null ) ).isTrue();
	}

	@Test
	public void validateAction_should_be_valid() {
		final ParameterizedAction action = createAction();
		final Map<String, String> map = new HashMap<>();
		map.put( "path", "path/to/other/parent" );
		map.put( "int1", "42" );
		map.put( "int2", "4711" );
		map.put( "bool", "true" );

		assertThat( Actions.validateAction( action, new MapValueProvider( map ) ) ).isTrue();
	}

	@Test
	public void validateAction_should_be_invalid_if_wrong_type() {
		final ParameterizedAction action = createAction();
		final Map<String, String> map = new HashMap<>();
		map.put( "path", "path/to/other/parent" );
		map.put( "int1", "42" );
		map.put( "int2", "Hi" );
		map.put( "bool", "false" );

		assertThat( Actions.validateAction( action, new MapValueProvider( map ) ) ).isFalse();
	}

	@Test
	public void validateAction_should_be_valid_with_missing_values() {
		final ParameterizedAction action = createAction();
		final Map<String, String> map = new HashMap<>();
		map.put( "path", "path/to/other/parent" );
		map.put( "int1", "42" );

		assertThat( Actions.validateAction( action, new MapValueProvider( map ) ) ).isTrue();
	}

	private ParameterizedAction createAction() {
		final Path path = Path.fromString( "path/to/parent" );
		final ParameterizedAttribute pathAttribute = new PathAttribute( path, "path" );
		final OutlineAttribute outlineAttribute = OutlineAttribute.create( new Rectangle( 0, 0, 0, 0 ) );

		final IdentifyingAttributes attributes =
				new IdentifyingAttributes( Arrays.asList( pathAttribute, outlineAttribute ) );

		final Element element = Element.create( "id", mock( Element.class ), attributes, new Attributes() );

		final ActionParameter a1 = new ActionParameter( "p1", "1", parameterTypeInteger, "int1" );
		final ActionParameter a2 = new ActionParameter( "p2", "2", parameterTypeInteger, "int2" );
		final ActionParameter a3 = new ActionParameter( "p3", "3", parameterTypeString );
		final ActionParameter a4 = new ActionParameter( "p4", "true", parameterTypeBoolean, "bool" );
		final ActionParameter a5 = new ActionParameter( "p5", Boolean.class.getName(), parameterTypeClass );

		return new ParameterizedAction( element, null, ParameterizedAction.class, "", a1, a2, a3, a4, a5 );
	}

	private static class MapValueProvider implements ValueProvider {
		private final Map<String, String> map;

		private MapValueProvider( final Map<String, String> map ) {
			this.map = map;
		}

		@Override
		public String get( final String key ) {
			return map.get( key );
		}
	}
}
