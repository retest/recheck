package de.retest.recheck.persistence.migration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

class MigrationPairs {

	private final List<Pair<Integer, XmlTransformer>> oldVersionToXSLT = new ArrayList<>();

	public MigrationPairs add( final int version, final XmlTransformer transformer ) {
		oldVersionToXSLT.add( new ImmutablePair<>( version, transformer ) );
		return this;
	}

	/**
	 * When renaming a root element class, we must add all migrations of the new class to the old class as well. This
	 * function is designed for this.
	 */
	public MigrationPairs addAll( final List<Pair<Integer, XmlTransformer>> migrations ) {
		oldVersionToXSLT.addAll( migrations );
		return this;
	}

	public List<Pair<Integer, XmlTransformer>> toList() {
		return oldVersionToXSLT;
	}
}
