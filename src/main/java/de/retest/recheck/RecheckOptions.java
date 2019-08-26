package de.retest.recheck;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.RecheckIgnoreUtil;
import lombok.Getter;

@Getter
public class RecheckOptions {

	private final FileNamerStrategy fileNamerStrategy;
	private final String suiteName;
	private final boolean reportUploadEnabled;
	private final Filter filter;

	public RecheckOptions( final FileNamerStrategy fileNamerStrategy, final String suiteName,
			final boolean reportUploadEnabled, final Filter filter ) {
		this.fileNamerStrategy = fileNamerStrategy;
		this.suiteName = suiteName;
		this.reportUploadEnabled = reportUploadEnabled;
		this.filter = filter;
	}

	public String getSuiteName() {
		return suiteName == null ? fileNamerStrategy.getTestClassName() : suiteName;
	}

	public static RecheckOptionsBuilder builder() {
		return new RecheckOptionsBuilder();
	}

	public static class RecheckOptionsBuilder {
		private FileNamerStrategy fileNamerStrategy = new MavenConformFileNamerStrategy();
		private String suiteName = null;
		private boolean reportUploadEnabled = false;
		private Filter filter = null;
		private final List<Filter> filterToAdd =
				new ArrayList<>( Arrays.asList( RecheckIgnoreUtil.loadRecheckIgnore() ) );
		private boolean addSuiteFilter = true;

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

		public RecheckOptionsBuilder filter( final Filter filter ) {
			addSuiteFilter = false;
			this.filter = filter;
			return this;
		}

		public RecheckOptionsBuilder addFilter( final Filter added ) {
			if ( filter == null ) {
				filterToAdd.add( added );
			} else {
				throw new IllegalStateException(
						"Cannot combine `filter()` and `add(filter)`. Use a `CompoundFilter` to do that." );
			}
			return this;
		}

		private File getSuitePath() {
			return fileNamerStrategy.createFileNamer( suiteName ).getFile( "" );
		}

		public RecheckOptions build() {
			if ( addSuiteFilter ) {
				filterToAdd.add( RecheckIgnoreUtil.loadRecheckSuiteIgnore( getSuitePath() ) );
			}
			if ( filter == null ) {
				filter = new CompoundFilter( filterToAdd );
			}
			return new RecheckOptions( fileNamerStrategy, suiteName, reportUploadEnabled, filter );
		}
	}
}
