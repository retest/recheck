package de.retest.ui.review;

import java.util.List;

import de.retest.ui.Path;
import de.retest.ui.descriptors.Element;

public class InsertChildren extends ComponentChanges {

	private final List<Element> insertedComponents;

	public InsertChildren( final Path parentPath, final List<Element> insertedComponents ) {
		super( parentPath );
		this.insertedComponents = insertedComponents;
	}

	public List<Element> getInsertedComponents() {
		return insertedComponents;
	}
}
