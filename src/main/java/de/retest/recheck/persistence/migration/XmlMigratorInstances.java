package de.retest.recheck.persistence.migration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.persistence.migration.transformers.AddRetestIdTestTransformer;
import de.retest.recheck.persistence.migration.transformers.ContainedComponents2ContainedElementsTransformer;
import de.retest.recheck.persistence.migration.transformers.IncompatibleChangesTransformer;
import de.retest.recheck.persistence.migration.transformers.Path2XPathTransformer;
import de.retest.recheck.persistence.migration.transformers.PathAndType2LowerCaseTransformer;
import de.retest.recheck.persistence.migration.transformers.WindowSuffixTransformer;

public enum XmlMigratorInstances {

	de_retest_recheck_ui_actions_ActionSequence() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 17, IncompatibleChangesTransformer.retestVersion2() ) //
					.add( 18, new WindowSuffixTransformer() ) //
					.add( 19, new Path2XPathTransformer() ) //
					.add( 20, new AddRetestIdTestTransformer() ) //
					.add( 21, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	},
	de_retest_recheck_suite_CapturedSuite() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.toList();
		}
	},
	de_retest_recheck_elementcollection_ElementCollection() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 14, IncompatibleChangesTransformer.retestVersion2() ) //
					.add( 15, new Path2XPathTransformer() ) //
					.add( 16, new AddRetestIdTestTransformer() ) //
					.add( 17, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	},
	de_retest_recheck_suite_ExecutableSuite() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 18, IncompatibleChangesTransformer.retestVersion2() ) //
					.add( 19, new WindowSuffixTransformer() ) //
					.add( 20, new Path2XPathTransformer() ) //
					.add( 21, new AddRetestIdTestTransformer() ) //
					.add( 22, new ContainedComponents2ContainedElementsTransformer() ) //
					.toList();
		}
	},
	de_retest_recheck_report_ReplayResult() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 20, IncompatibleChangesTransformer.recheckVersion1() ) //
					.toList();
		}
	},
	de_retest_recheck_test_Test() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.toList();
		}
	},
	de_retest_recheck_ui_descriptors_SutState() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 1, new AddRetestIdTestTransformer() ) //
					.add( 2, new ContainedComponents2ContainedElementsTransformer() ) //
					.add( 3, new PathAndType2LowerCaseTransformer() ) //
					.toList();
		}
	},
	de_retest_recheck_report_TestReport() {
		@Override
		List<Pair<Integer, XmlTransformer>> migrations() {
			return new MigrationPairs() //
					.add( 20, IncompatibleChangesTransformer.recheckVersion1() ) //
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
