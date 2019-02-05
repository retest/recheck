package de.retest.recheck.ui.components;

import de.retest.recheck.ui.descriptors.RootElement;

public interface RootContainer<T> extends ComponentContainer<T> {

	RootElement getRootElement();

	String getScreenId();

}
