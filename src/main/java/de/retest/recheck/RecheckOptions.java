package de.retest.recheck;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import de.retest.recheck.persistence.FileNamer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor( access = AccessLevel.PRIVATE )
public class RecheckOptions {

	@Getter
	private final FileNamerStrategy fileNamerStrategy;
	private final String suiteName;
	@Getter
	private final boolean reportUploadEnabled;
	private final Filter filter;
	private final List<Filter> filterToAdd;

	public static RecheckOptionsBuilder builder() {
		return new RecheckOptionsBuilder();
	}

	public String getSuiteName() {
		if ( suiteName != null ) {
			return suiteName;
		}
		return fileNamerStrategy.getTestClassName();
	}

	public Filter getFilter() {
		if ( filter != null ) {
			return filter;
		}
		final ArrayList<Filter> filters = new ArrayList<>( filterToAdd );
		filters.add( RecheckIgnoreUtil.loadRecheckIgnore() );
		filters.add( RecheckIgnoreUtil.loadRecheckSuiteIgnore( getSuitePath() ) );
		return new CompoundFilter( filters );
	}

	private File getSuitePath() {
		final FileNamer fileNamer = fileNamerStrategy.createFileNamer( getSuiteName() );
		return fileNamer.getFile( Properties.GOLDEN_MASTER_FILE_EXTENSION );
	}

	public static class RecheckOptionsBuilder {

		private FileNamerStrategy fileNamerStrategy = new MavenConformFileNamerStrategy();
		private String suiteName = null;
		private boolean reportUploadEnabled = false;
		private Filter filter = null;
		private final List<Filter> filterToAdd = new ArrayList<>();

		private RecheckOptionsBuilder() {}

		public RecheckOptionsBuilder fileNamerStrategy( final FileNamerStrategy fileNamerStrategy ) {
			this.fileNamerStrategy = fileNamerStrategy;
			return this;
		}

		public RecheckOptionsBuilder suiteName( final String suiteName ) {
			this.suiteName = suiteName;
			return this;
		}

		public RecheckOptionsBuilder enableReportUpload() {
			reportUploadEnabled = true;
			return this;
		}

		public RecheckOptionsBuilder setFilter( final Filter filter ) {
			this.filter = filter;
			return this;
		}

		public RecheckOptionsBuilder addFilter( final Filter added ) {
			if ( filter == null ) {
				filterToAdd.add( added );
			} else {
				throw new IllegalStateException( "Cannot combine `setFilter(Filter)` and `addFilter(Filter)`." );
			}
			return this;
		}

		public RecheckOptions build() {
			return new RecheckOptions( fileNamerStrategy, suiteName, reportUploadEnabled, filter, filterToAdd );
		}
	}
}
