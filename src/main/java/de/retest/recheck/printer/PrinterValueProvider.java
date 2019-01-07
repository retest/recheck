package de.retest.recheck.printer;

import java.io.Serializable;

import de.retest.ui.DefaultValueFinder;
import de.retest.ui.descriptors.IdentifyingAttributes;

public interface PrinterValueProvider {

	boolean isDefault( String key, Serializable value );

	static PrinterValueProvider of( DefaultValueFinder finder, IdentifyingAttributes attributes ) {
		return (key, value) -> finder != null && finder.isDefaultValue( attributes, key, value );
	}
}
