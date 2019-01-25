package de.retest.ui.components;

import de.retest.ui.Environment;
import de.retest.ui.Path;

public interface ComponentCreator<T> {

	public abstract Component<T> create( Path path, T component, Environment<T> environment );

}
