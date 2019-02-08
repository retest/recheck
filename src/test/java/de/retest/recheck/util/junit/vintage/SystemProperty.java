package de.retest.recheck.util.junit.vintage;

import org.junit.rules.ExternalResource;

public class SystemProperty extends ExternalResource {

	private final String key;
	private String oldValue;
	private final String value;
	private boolean started;

	public SystemProperty( final String key, final String value ) {
		this.key = key;
		this.value = value;
	}

	public SystemProperty( final String key ) {
		this.key = key;
		value = System.getProperty( key );
	}

	public void setValue( final String value ) {
		if ( !started ) {
			throw new IllegalStateException( "This method is only to set value in test methods!" );
		}
		if ( value == null ) {
			System.clearProperty( key );
		} else {
			System.setProperty( key, value );
		}
	}

	public String getOldValue() {
		return oldValue;
	}

	@Override
	protected void before() {
		oldValue = System.getProperty( key );
		if ( value == null ) {
			System.clearProperty( key );
		} else {
			System.setProperty( key, value );
		}
		started = true;
	}

	@Override
	protected void after() {
		if ( oldValue == null ) {
			System.clearProperty( key );
		} else {
			System.setProperty( key, oldValue );
		}
		started = false;
	}
}
