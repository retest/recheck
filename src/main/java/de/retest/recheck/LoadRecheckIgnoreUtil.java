package de.retest.recheck;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import de.retest.recheck.elementcollection.ElementCollection;
import de.retest.recheck.elementcollection.RecheckIgnore;
import de.retest.recheck.persistence.PersistenceFactory;
import de.retest.recheck.ui.IgnoredTypes;

public class LoadRecheckIgnoreUtil {
	public static void loadRecheckIgnore() {
		RecheckIgnore.loadIgnored(
				new PersistenceFactory( getXmlTransformer() ).<ElementCollection> getPersistence(),
				new IgnoredTypes.NoIgnoredTypes() );
	}
}
