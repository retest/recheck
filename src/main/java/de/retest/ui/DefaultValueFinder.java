package de.retest.ui;

import java.io.Serializable;

import de.retest.ui.descriptors.IdentifyingAttributes;

public interface DefaultValueFinder {

	/**
	 * Checks if the combination of {@code identifyingAttributes}, {@code attributeKey}, and {@code attributeValue}
	 * represents a default value. Default values are not persisted to save lots of memory and reduce noise.
	 *
	 * @param identifyingAttributes
	 *            identifying attributes
	 * @param attributeKey
	 *            attribute key
	 * @param attributeValue
	 *            attribute value
	 * @return {@code true} if the value is a default
	 */
	boolean isDefaultValue( final IdentifyingAttributes identifyingAttributes, final String attributeKey,
			Serializable attributeValue );
}
