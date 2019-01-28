package de.retest.persistence.migration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.persistence.migration.transformers.AddRetestIdTestTransformer;
import de.retest.persistence.migration.transformers.ContainedComponents2ContainedElementsTransformer;
import de.retest.persistence.migration.transformers.IncompatibleChangesTransformer;
import de.retest.persistence.migration.transformers.Path2XPathTransformer;
import de.retest.persistence.migration.transformers.WindowSuffixTransformer;

public enum XmlMigratorInstances {

	de_retest_ui_actions_ActionSequence() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 17, IncompatibleChangesTransformer.version2() ) //
					.add( 18, new WindowSuffixTransformer() ) //
					.add( 19, new Path2XPathTransformer() ) //
					.add( 20, new AddRetestIdTestTransformer() ) //
					.add( 21, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	},
	de_retest_suite_CapturedSuite() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.toList();
		}
	},
	de_retest_elementcollection_ElementCollection() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 14, IncompatibleChangesTransformer.version2() ) //
					.add( 15, new Path2XPathTransformer() ) //
					.add( 16, new AddRetestIdTestTransformer() ) //
					.add( 17, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	},
	de_retest_suite_ExecutableSuite() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 18, IncompatibleChangesTransformer.version2() ) //
					.add( 19, new WindowSuffixTransformer() ) //
					.add( 20, new Path2XPathTransformer() ) //
					.add( 21, new AddRetestIdTestTransformer() ) //
					.add( 22, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	},
	de_retest_report_ReplayResult() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 15, IncompatibleChangesTransformer.version2() ) //
					.add( 16, new Path2XPathTransformer() ) //
					.add( 17, new AddRetestIdTestTransformer() ) //
					.add( 18, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	},
	de_retest_test_Test() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.toList();
		}
	},
	de_retest_ui_descriptors_SutState() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 1, new AddRetestIdTestTransformer() ) //
					.add( 2, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	};

	public static XmlMigratorInstances get( final String dataTypeClass ) {
		try {
			return XmlMigratorInstances.valueOf( dataTypeClass.replaceAll( "\\.", "_" ) );
		} catch ( final IllegalArgumentException exc ) {
			return null;
		}
	}

	abstract List<Pair<Integer, XmlTransformer>> migrations();

	public List<XmlTransformer> getXmlTransformersFor( final int oldVersion ) {
		final List<XmlTransformer> result = new ArrayList<>();
		for ( final Pair<Integer, XmlTransformer> migration : migrations() ) {
			if ( migration.getLeft() >= oldVersion ) {
				result.add( migration.getRight() );
			}
		}
		return result;
	}
}
