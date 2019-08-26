package de.retest.recheck;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor( access = AccessLevel.PRIVATE )
@Getter
public class RecheckOptions {

	private final FileNamerStrategy fileNamerStrategy;
	private final String suiteName;
	private final boolean reportUploadEnabled;
	private final Filter filter;

	public static RecheckOptionsBuilder builder() {
		return new RecheckOptionsBuilder();
	}

	public static class RecheckOptionsBuilder {
		private FileNamerStrategy fileNamerStrategy = new MavenConformFileNamerStrategy();
		private String suiteName = fileNamerStrategy.getTestClassName();
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
				throw new IllegalStateException(
						"Cannot combine `setFilter()` and `add(Filter)`. Use a `CompoundFilter` to do that." );
			}
			return this;
		}

		public RecheckOptions build() {
			if ( filter == null ) {
				filterToAdd.add( RecheckIgnoreUtil.loadRecheckIgnore() );
				filterToAdd.add( RecheckIgnoreUtil.loadRecheckSuiteIgnore( getSuitePath() ) );
				filter = new CompoundFilter( filterToAdd );
			}
			return new RecheckOptions( fileNamerStrategy, suiteName, reportUploadEnabled, filter );
		}

		private File getSuitePath() {
			return fileNamerStrategy.createFileNamer( suiteName ).getFile( "" );
		}
	}
}
