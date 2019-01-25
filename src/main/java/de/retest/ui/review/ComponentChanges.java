package de.retest.ui.review;

import de.retest.ui.Path;

public abstract class ComponentChanges {

	protected final Path componentPath;

	public ComponentChanges( final Path componentPath ) {
		this.componentPath = componentPath;
	}

	public Path getOriginalPath() {
		return componentPath;
	}
}
