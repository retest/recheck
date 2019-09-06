package de.retest.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.retest.recheck.persistence.xml.util.StdXmlClassesProvider;
import de.retest.recheck.util.ApprovalsUtil;

class XsdRegressionTest {

	/**
	 * Check the generated XSD of our XML data classes for differences, which possibly indicate the need of a
	 * corresponding migrator. See <code>src/test/resources/de/retest/util/XsdRegressionTest.checkXsd/</code> for
	 * approved/received files.
	 *
	 * @param clazz
	 *            The class to be serialized and checked for differences.
	 */
	@ParameterizedTest
	@MethodSource( "getXmlDataClasses" )
	void checkXsd( final Class<?> clazz ) throws Exception {
		ApprovalsUtil.verifyMultiXsd( clazz );
	}

	// Wrapper since @MethodSource doesn't support varargs.
	static Class<?>[] getXmlDataClasses() {
		return StdXmlClassesProvider.getXmlDataClasses();
	}

}
