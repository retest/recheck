package de.retest.recheck.persistence.bin;

import com.esotericsoftware.kryo.kryo5.Kryo;

public class KryoRegister {

	public static void addRecheckClasses( final Kryo kryo ) {
		kryo.setRegistrationRequired( false ); // TODO remove, see #836
		kryo.setWarnUnregisteredClasses( true );

		kryo.register( de.retest.recheck.ui.Path.class );
		kryo.register( de.retest.recheck.ui.PathElement.class );
		kryo.register( de.retest.recheck.ui.image.Screenshot.class );
		kryo.register( de.retest.recheck.ui.image.Screenshot.ImageType.class );
		kryo.register( de.retest.recheck.ui.review.GoldenMasterSource.class );

		kryo.register( de.retest.recheck.ui.descriptors.GroundState.class );
		kryo.register( de.retest.recheck.ui.descriptors.RootElement.class );
		kryo.register( de.retest.recheck.ui.descriptors.Attributes.class );
		kryo.register( de.retest.recheck.ui.descriptors.Element.class );
		kryo.register( de.retest.recheck.ui.descriptors.IdentifyingAttributes.class );
		kryo.register( de.retest.recheck.ui.descriptors.OutlineAttribute.class );
		kryo.register( de.retest.recheck.ui.descriptors.PathAttribute.class );
		kryo.register( de.retest.recheck.ui.descriptors.SuffixAttribute.class );
		kryo.register( de.retest.recheck.ui.descriptors.TextAttribute.class );
		kryo.register( de.retest.recheck.ui.descriptors.StringAttribute.class );

		kryo.register( de.retest.recheck.ui.diff.StateDifference.class );
		kryo.register( de.retest.recheck.ui.diff.RootElementDifference.class );
		kryo.register( de.retest.recheck.ui.diff.ElementDifference.class );
		kryo.register( de.retest.recheck.ui.diff.IdentifyingAttributesDifference.class );
		kryo.register( de.retest.recheck.ui.diff.InsertedDeletedElementDifference.class );
		kryo.register( de.retest.recheck.ui.diff.AttributeDifference.class );
		kryo.register( de.retest.recheck.ui.diff.AttributesDifference.class );
		kryo.register( de.retest.recheck.ui.diff.ElementIdentificationWarning.class );
		kryo.register( de.retest.recheck.ui.diff.meta.MetadataDifference.class );
		kryo.register( de.retest.recheck.ui.diff.meta.MetadataElementDifference.class );

		kryo.register( de.retest.recheck.report.TestReport.class );
		kryo.register( de.retest.recheck.report.SuiteReplayResult.class );
		kryo.register( de.retest.recheck.report.TestReplayResult.class );
		kryo.register( de.retest.recheck.report.ActionReplayResult.class );

		// TODO add more, see #836
	}

	public static void addUsedJdkClasses( final Kryo kryo ) {
		kryo.register( java.util.ArrayList.class );
		kryo.register( java.util.Date.class );
		kryo.register( java.util.TreeMap.class );
		kryo.register( java.util.HashSet.class );
		kryo.register( java.util.LinkedHashSet.class );

		kryo.register( java.util.Collections.EMPTY_LIST.getClass() );
		kryo.register( java.util.Collections.EMPTY_MAP.getClass() );
		kryo.register( java.util.Collections.EMPTY_SET.getClass() );
		kryo.register( java.util.Collections.singletonList( Object.class ).getClass() );

		kryo.register( byte[].class );
		kryo.register( java.awt.Rectangle.class );

		kryo.register( com.google.common.collect.TreeMultiset.class )
				.setInstantiator( com.google.common.collect.TreeMultiset::create );

		// TODO add more, see #836
	}

}
