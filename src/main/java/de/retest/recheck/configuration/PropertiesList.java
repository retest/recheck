package de.retest.recheck.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

class PropertiesList extends Properties {

	private static final long serialVersionUID = 1L;

	private final List<Properties> propertiesList;

	public PropertiesList( final Properties... properties ) {
		propertiesList = new ArrayList<>( Arrays.asList( properties ) );
	}

	@Override
	public String getProperty( final String key ) {
		for ( final Properties properties : propertiesList ) {
			final String value = properties.getProperty( key );
			if ( value != null ) {
				return value;
			}
		}
		return null;
	}

	@Override
	public String getProperty( final String key, final String defaultValue ) {
		for ( final Properties properties : propertiesList ) {
			final String value = properties.getProperty( key );
			if ( value != null ) {
				return value;
			}
		}
		return defaultValue;
	}

	@Override
	public synchronized boolean containsKey( final Object key ) {
		for ( final Properties properties : propertiesList ) {
			if ( properties.containsKey( key ) ) {
				return true;
			}
		}
		return false;
	}

	// UnsupportedOperations

	@Override
	public synchronized Object setProperty( final String key, final String value ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Object remove( final Object key ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean contains( final Object value ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue( final Object value ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Enumeration<Object> elements() {
		final Vector<Object> result = new Vector<>();
		for ( final Properties properties : propertiesList ) {
			result.addAll( properties.values() );
		}
		return result.elements();
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Object get( final Object key ) {
		for ( final Properties properties : propertiesList ) {
			final Object value = properties.get( key );
			if ( value != null ) {
				return value;
			}
		}
		return null;
	}

	@Override
	public synchronized boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Enumeration<Object> keys() {
		final Vector<Object> result = new Vector<>();
		for ( final Properties properties : propertiesList ) {
			result.addAll( properties.keySet() );
		}
		return result.elements();
	}

	@Override
	public Set<Object> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Object clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean equals( final Object o ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized int hashCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void list( final PrintStream out ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void list( final PrintWriter out ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void load( final InputStream inStream ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void load( final Reader reader ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void loadFromXML( final InputStream in ) throws IOException, InvalidPropertiesFormatException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<?> propertyNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Object put( final Object key, final Object value ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void putAll( final Map<? extends Object, ? extends Object> t ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void save( final OutputStream out, final String comments ) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void rehash() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void store( final OutputStream out, final String comments ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void store( final Writer writer, final String comments ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void storeToXML( final OutputStream os, final String comment ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void storeToXML( final OutputStream os, final String comment, final String encoding )
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> stringPropertyNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized String toString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

}
