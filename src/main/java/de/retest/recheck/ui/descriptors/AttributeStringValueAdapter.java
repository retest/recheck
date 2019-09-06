package de.retest.recheck.ui.descriptors;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.retest.recheck.ui.descriptors.AttributeStringValueAdapter.StringValue;

public class AttributeStringValueAdapter extends XmlAdapter<StringValue, String> {

	public static class StringValue {

		@XmlValue
		public String value;

		// For JAXB.
		protected StringValue() {}

		public StringValue( final String value ) {
			this.value = value;
		}
	}

	@Override
	public String unmarshal( final StringValue v ) throws Exception {
		return v.value;
	}

	@Override
	public StringValue marshal( final String v ) throws Exception {
		return new StringValue( v );
	}

}
