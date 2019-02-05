package de.retest.recheck;

import java.util.Set;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.IgnoredTypes;
import de.retest.recheck.ui.descriptors.GroundState;

public interface ExecutingTestContext {

	@SuppressWarnings( "rawtypes" )
	Environment getEnvironment();

	Set<Class<?>> getXmlDataClasses();

	GroundState createGroundState();

	<T extends Persistable> Persistence<T> getPersistence();

	IgnoredTypes getIgnoredTypes();

}
