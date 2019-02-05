package de.retest.recheck.persistence.xml;

import de.retest.recheck.persistence.Persistable;

public class TestPersistable extends Persistable {

	private static final long serialVersionUID = 1L;

	public static final String CLASS_NAME = TestPersistable.class.getName();
	public static final int PERSISTENCE_VERSION = 9001;

	public static final String XML = generateXmlWith( CLASS_NAME, PERSISTENCE_VERSION );

	public static final String XML_WITH_WRONG_VERSION = generateXmlWith( CLASS_NAME, PERSISTENCE_VERSION - 1 );
	public static final String XML_WITH_RENAMED_CLASS =
			generateXmlWith( "de.retest.persistence.xml.ClassWithOldName", PERSISTENCE_VERSION - 1 );

	private static String generateXmlWith( final String className, final int persistenceVersion ) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><reTestXmlDataContainer  "
				+ " reTestVersion=\"2342\" dataType=\"" + className + "\" dataTypeVersion=\"" + persistenceVersion
				+ "\">" + "<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"testPersistable\">"
				+ "...</data></reTestXmlDataContainer>";
	}

	public TestPersistable() {
		super( PERSISTENCE_VERSION );
	}
}
