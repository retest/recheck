package de.retest.configuration;

public class Property {
	private final String key;
	private final String value;

	public Property( final String propertyKey ) {
		key = propertyKey;
		value = System.getProperty( key );
	}

	public Property( final String key, final String value ) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}
}
