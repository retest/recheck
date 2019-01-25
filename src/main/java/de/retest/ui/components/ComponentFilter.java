package de.retest.ui.components;

public interface ComponentFilter<T> {

	boolean isComponentIgnored( final T component );
}
