package de.retest.recheck.persistence.migration.transformers;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import de.retest.recheck.persistence.migration.XmlTransformer;

public class IncompatibleChangesTransformer extends XmlTransformer {

	public static final String RETEST_VERSION_2_ERROR_MESSAGE =
			"The internal changes (ParameterizedAction) are incompatible. Please re-record your actions with retest version 2+.";

	public static final String RECHECK_VERSION_1_ERROR_MESSAGE =
			"The internal changes are incompatible. Please re-run your tests with recheck version 1+.";

	private final String msg;

	public static IncompatibleChangesTransformer retestVersion2() {
		return new IncompatibleChangesTransformer( RETEST_VERSION_2_ERROR_MESSAGE );
	}

	public static IncompatibleChangesTransformer recheckVersion1() {
		return new IncompatibleChangesTransformer( RECHECK_VERSION_1_ERROR_MESSAGE );
	}

	private IncompatibleChangesTransformer( final String msg ) {
		this.msg = msg;
	}

	@Override
	protected void reset() {
		throw new Error( msg );
	}

	@Override
	public void convert( final XMLEvent event, final XMLEventWriter eventWriter ) throws XMLStreamException {
		throw new Error( msg );
	}

}
