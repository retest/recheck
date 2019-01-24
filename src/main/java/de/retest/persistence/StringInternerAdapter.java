package de.retest.persistence;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

// Using code from https://jaxb.java.net/tutorial/section_5_5-Interning-Strings.html
public class StringInternerAdapter extends XmlAdapter<String, String> {

	@Override
	public String marshal( final String value ) throws Exception {
		return value;
	}

	@Override
	public String unmarshal( final String value ) throws Exception {
		return DatatypeConverter.parseString( value ).intern();
	}

}
