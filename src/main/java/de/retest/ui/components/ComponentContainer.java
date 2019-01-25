package de.retest.ui.components;

import de.retest.ui.descriptors.IdentifyingAttributes;

public interface ComponentContainer<T> extends Component<T> {

	String getTextWithComponents();

	java.util.List<Component<T>> getTargetableComponents();

	/**
	 * @return All components, deeply retrieved.
	 */
	java.util.List<Component<T>> getAllComponents();

	/**
	 * @return Only direct child components (of the next deeper level).
	 */
	java.util.List<Component<T>> getChildComponents();

	Component<T> getPeerComponent( T component );

	Component<T> getBestMatch( IdentifyingAttributes identifyingAttributes );
}
