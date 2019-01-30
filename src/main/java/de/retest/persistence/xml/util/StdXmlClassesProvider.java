package de.retest.persistence.xml.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.retest.recheck.persistence.xml.ReTestXmlDataContainer;
import de.retest.recheck.suite.CapturedSuite;
import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.actions.ActionParameter;
import de.retest.recheck.ui.actions.ActionSequence;
import de.retest.recheck.ui.actions.ParameterizedAction;
import de.retest.recheck.ui.descriptors.CodeLocAttribute;
import de.retest.recheck.ui.descriptors.ContextAttribute;
import de.retest.recheck.ui.descriptors.DefaultAttribute;
import de.retest.recheck.ui.descriptors.OutlineAttribute;
import de.retest.recheck.ui.descriptors.ParameterType;
import de.retest.recheck.ui.descriptors.PathAttribute;
import de.retest.recheck.ui.descriptors.ScreenshotAttributeDifference;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.SuffixAttribute;
import de.retest.recheck.ui.descriptors.TextAttribute;
import de.retest.recheck.ui.descriptors.WeightedTextAttribute;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.DurationDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;

public class StdXmlClassesProvider {

	static {
		ParameterType.registerStdParameterTypes();
	}

	private static final Class<?>[] classes = new Class<?>[] { //
			ActionParameter.class, //
			ParameterizedAction.class, //
			ActionSequence.class, //
			CapturedSuite.class, //
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
