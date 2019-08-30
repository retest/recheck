package de.retest.recheck.ignore;

import lombok.Getter;

@Getter
public class FilterNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String projectFilter;

	public FilterNotFoundException( final String name, final String projectFilter ) {
		super( "No filter with given name '" + name + "' found in either the project filter folder '" + projectFilter
				+ "' and the predefined default filter." );
		this.name = name;
		this.projectFilter = projectFilter;
	}

}
