package de.retest;

import java.util.Set;

import de.retest.persistence.Persistable;
import de.retest.persistence.Persistence;
import de.retest.ui.Environment;
import de.retest.ui.IgnoredTypes;
import de.retest.ui.descriptors.GroundState;

public interface ExecutingTestContext {

	@SuppressWarnings( "rawtypes" )
	Environment getEnvironment();

	Set<Class<?>> getXmlDataClasses();

	GroundState createGroundState();

	<T extends Persistable> Persistence<T> getPersistence();

	IgnoredTypes getIgnoredTypes();

}
