package de.retest.recheck.ignore;

import java.util.Optional;

import lombok.Getter;

@Getter
public class FilterNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2L;

	private final String filterName;
	private final String projectFilterDir;

	public FilterNotFoundException( final String filterName ) {
		super( "No filter with name '" + filterName
				+ "' found in the user home and among the predefined default filters. (No project to search in available.)" );
		this.filterName = filterName;
		projectFilterDir = null;
	}

	public FilterNotFoundException( final String filterName, final String projectFilterDir ) {
		super( "No filter with name '" + filterName + "' found in the project filter folder '" + projectFilterDir
				+ "', the user home and among the predefined default filters." );
		this.filterName = filterName;
		this.projectFilterDir = projectFilterDir;
	}

	public Optional<String> getProjectFilterDir() {
		return Optional.ofNullable( projectFilterDir );
	}

}
