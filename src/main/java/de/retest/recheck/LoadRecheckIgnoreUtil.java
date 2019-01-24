package de.retest.recheck;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import de.retest.elementcollection.ElementCollection;
import de.retest.elementcollection.RecheckIgnore;
import de.retest.persistence.PersistenceFactory;
import de.retest.ui.IgnoredTypes;

public class LoadRecheckIgnoreUtil {
	public static void loadRecheckIgnore() {
		RecheckIgnore.loadIgnored(
				new PersistenceFactory( getXmlTransformer() ).<ElementCollection> getPersistence(),
				new IgnoredTypes.NoIgnoredTypes() );
	}
}
