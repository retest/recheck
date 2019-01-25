package de.retest.ui.components;

import de.retest.ui.descriptors.RootElement;

public interface RootContainer<T> extends ComponentContainer<T> {

	RootElement getRootElement();

	String getScreenId();

}
