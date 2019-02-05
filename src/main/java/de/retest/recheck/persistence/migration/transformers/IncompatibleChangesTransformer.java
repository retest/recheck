package de.retest.recheck.persistence.migration.transformers;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import de.retest.recheck.persistence.migration.XmlTransformer;

public class IncompatibleChangesTransformer extends XmlTransformer {

	public static final String VERSION_2_ERROR_MESSAGE =
			"The internal changes (ParameterizedAction) are incompatible. Please re-record your actions with retest version 2+.";

	private final String msg;

	public static IncompatibleChangesTransformer version2() {
		return new IncompatibleChangesTransformer( VERSION_2_ERROR_MESSAGE );
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
