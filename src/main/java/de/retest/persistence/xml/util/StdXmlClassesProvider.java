package de.retest.persistence.xml.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.retest.elementcollection.ElementCollection;
import de.retest.persistence.xml.ReTestXmlDataContainer;
import de.retest.suite.CapturedSuite;
import de.retest.suite.ExecutableSuite;
import de.retest.ui.actions.ActionParameter;
import de.retest.ui.actions.ActionSequence;
import de.retest.ui.actions.ParameterizedAction;
import de.retest.ui.descriptors.CodeLocAttribute;
import de.retest.ui.descriptors.ContextAttribute;
import de.retest.ui.descriptors.DefaultAttribute;
import de.retest.ui.descriptors.OutlineAttribute;
import de.retest.ui.descriptors.ParameterType;
import de.retest.ui.descriptors.PathAttribute;
import de.retest.ui.descriptors.ScreenshotAttributeDifference;
import de.retest.ui.descriptors.StringAttribute;
import de.retest.ui.descriptors.SuffixAttribute;
import de.retest.ui.descriptors.TextAttribute;
import de.retest.ui.descriptors.WeightedTextAttribute;
import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.diff.AttributesDifference;
import de.retest.ui.diff.DurationDifference;
import de.retest.ui.diff.ElementDifference;
import de.retest.ui.diff.IdentifyingAttributesDifference;
import de.retest.ui.diff.InsertedDeletedElementDifference;
import de.retest.ui.diff.RootElementDifference;
import de.retest.ui.diff.StateDifference;

public class StdXmlClassesProvider {

	static {
		ParameterType.registerStdParameterTypes();
	}

	private static final Class<?>[] classes = new Class<?>[] { //
			ActionParameter.class, //
			ParameterizedAction.class, //
			ActionSequence.class, //
			CapturedSuite.class, //
			ElementCollection.class, //
			ExecutableSuite.class, //
			ReTestXmlDataContainer.class, //
			de.retest.test.Test.class, //
			// we need these to handle subtypes properly: (see RET-357)
			DefaultAttribute.class, //
			PathAttribute.class, //
			SuffixAttribute.class, //
			StringAttribute.class, //
			TextAttribute.class, //
			CodeLocAttribute.class, //
			ContextAttribute.class, //
			OutlineAttribute.class, //
			WeightedTextAttribute.class, //

			DurationDifference.class, //
			AttributesDifference.class, //
			AttributeDifference.class, //
			ScreenshotAttributeDifference.class, //
			StateDifference.class, //
			IdentifyingAttributesDifference.class, //
			InsertedDeletedElementDifference.class, //
			RootElementDifference.class, //
			ElementDifference.class, //
	};

	public static Class<?>[] getXmlDataClasses( final Class<?>... additionalClazzes ) {
		return getXmlDataClasses( new HashSet<>( Arrays.asList( additionalClazzes ) ) );
	}

	public static Class<?>[] getXmlDataClasses( final Set<Class<?>> additionalClazzes ) {
		final Set<Class<?>> clazzes = new HashSet<>( Arrays.asList( classes ) );
		clazzes.addAll( additionalClazzes );
		return clazzes.toArray( new Class<?>[clazzes.size()] );
	}
}
