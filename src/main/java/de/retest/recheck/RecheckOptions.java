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

/**
 * This class configures the behaviour of {@link Recheck} and their implementations.
 */
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public class RecheckOptions {

	private final FileNamerStrategy fileNamerStrategy;
	private final String suiteName;
	private final boolean reportUploadEnabled;
	private final Filter filter;
	private final List<Filter> filterToAdd;

	public static RecheckOptionsBuilder builder() {
		return new RecheckOptionsBuilder();
	}

	/**
	 * Gets the configured {@link FileNamerStrategy} which should be used to identify the tests, locate the golden
	 * masters and report files.
	 * 
	 * @return The {@link FileNamerStrategy} to use for locating golden masters and reports.
	 */
	public FileNamerStrategy getFileNamerStrategy() {
		return fileNamerStrategy;
	}

	/**
	 * Gets the suite name which overwrites {@link FileNamerStrategy#getTestClassName()}.
	 *
	 * @return The suite name to use for identifying the golden master.
	 * @implNote If no suite name is provided, the {@link FileNamerStrategy#getTestClassName()} is used.
	 */
	public String getSuiteName() {
		if ( suiteName != null ) {
			return suiteName;
		}
		return fileNamerStrategy.getTestClassName();
	}

	/**
	 * If the report should be uploaded to <a href="https://retest.de/rehub/">rehub</a>.
	 *
	 * @return If the report should be uploaded.
	 * @see Rehub
	 */
	public boolean isReportUploadEnabled() {
		return reportUploadEnabled;
	}

	/**
	 * Gets the configured filter which is used for printing the report after a test.
	 *
	 * @return The filter to use for printing the report.
	 * @implNote If no filter is provided, the default filters are used.
	 */
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

		/**
		 * Configures the {@link FileNamerStrategy} to identify tests, locate golden masters and report files.
		 *
		 * @param fileNamerStrategy
		 *            The strategy to use, defaults to {@link MavenConformFileNamerStrategy}
		 * @return self
		 */
		public RecheckOptionsBuilder fileNamerStrategy( final FileNamerStrategy fileNamerStrategy ) {
			this.fileNamerStrategy = fileNamerStrategy;
			return this;
		}

		/**
		 * Overwrites the suite name from {@link FileNamerStrategy}.
		 *
		 * @param suiteName
		 *            The suite name to identify the golden master. Default:
		 *            {@link FileNamerStrategy#getTestClassName()}.
		 * @return self
		 */
		public RecheckOptionsBuilder suiteName( final String suiteName ) {
			this.suiteName = suiteName;
			return this;
		}

		/**
		 * Enables upload to <a href="https://retest.de/rehub/">rehub</a> so that all reports are stored there. Default:
		 * false.
		 *
		 * @return self
		 */
		public RecheckOptionsBuilder enableReportUpload() {
			reportUploadEnabled = true;
			return this;
		}

		/**
		 * Overwrites the filter used for printing the report after a test. The filter cannot be used in conjunction
		 * with {@link #addFilter(Filter)}.
		 *
		 * @param filter
		 *            The filter to use for printing the differences. Default: Loads the ignore files.
		 * @return self
		 */
		public RecheckOptionsBuilder setFilter( final Filter filter ) {
			this.filter = filter;
			return this;
		}

		/**
		 * Appends a filter to the default filters. Cannot be used once a filter is overwritten with
		 * {@link #setFilter(Filter)}.
		 *
		 * @param added
		 *            The filter to add.
		 * @return self
		 * @see #setFilter(Filter)
		 */
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
