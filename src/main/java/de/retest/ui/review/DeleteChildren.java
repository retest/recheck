package de.retest.ui.review;

import java.util.List;

import de.retest.ui.Path;
import de.retest.ui.descriptors.IdentifyingAttributes;

public class DeleteChildren extends ComponentChanges {

	private final List<IdentifyingAttributes> deletedComponents;

	public DeleteChildren( final Path parentPath, final List<IdentifyingAttributes> deletedComponents ) {
		super( parentPath );
		this.deletedComponents = deletedComponents;
	}

	public List<IdentifyingAttributes> getDeletedComponents() {
		return deletedComponents;
	}

}
