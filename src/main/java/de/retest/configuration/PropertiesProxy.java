package de.retest.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

class PropertiesProxy extends Properties {

	private static final long serialVersionUID = 1L;

	private final Properties parent;

	public PropertiesProxy( final Properties parent ) {
		this.parent = parent;
	}

	@Override
	public Object setProperty( final String key, final String value ) {
		return parent.setProperty( key, value );
	}

	@Override
	public void load( final Reader reader ) throws IOException {
		parent.load( reader );
	}

	@Override
	public int size() {
		return parent.size();
	}

	@Override
	public boolean isEmpty() {
		return parent.isEmpty();
	}

	@Override
	public Enumeration<Object> keys() {
		return parent.keys();
	}

	@Override
	public Enumeration<Object> elements() {
		return parent.elements();
	}

	@Override
	public boolean contains( final Object value ) {
		return parent.contains( value );
	}

	@Override
	public boolean containsValue( final Object value ) {
		return parent.containsValue( value );
	}

	@Override
	public boolean containsKey( final Object key ) {
		return parent.containsKey( key );
	}

	@Override
	public Object get( final Object key ) {
		return parent.get( key );
	}

	@Override
	public void load( final InputStream inStream ) throws IOException {
		parent.load( inStream );
	}

	@Override
	public Object put( final Object key, final Object value ) {
		return parent.put( key, value );
	}

	@Override
	public Object remove( final Object key ) {
		return parent.remove( key );
	}

	@Override
	public void putAll( final Map<? extends Object, ? extends Object> t ) {
		parent.putAll( t );
	}

	@Override
	public void clear() {
		parent.clear();
	}

	@Override
	public Set<Object> keySet() {
		return parent.keySet();
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return parent.entrySet();
	}

	@Override
	public Collection<Object> values() {
		return parent.values();
	}

	@Override
	public void save( final OutputStream out, final String comments ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void store( final Writer writer, final String comments ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void store( final OutputStream out, final String comments ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void loadFromXML( final InputStream in ) throws IOException, InvalidPropertiesFormatException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeToXML( final OutputStream os, final String comment ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeToXML( final OutputStream os, final String comment, final String encoding ) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty( final String key ) {
		return parent.getProperty( key );
	}

	@Override
	public String getProperty( final String key, final String defaultValue ) {
		return parent.getProperty( key, defaultValue );
	}

	@Override
	public Enumeration<?> propertyNames() {
		return parent.propertyNames();
	}

	@Override
	public Set<String> stringPropertyNames() {
		return parent.stringPropertyNames();
	}

	@Override
	public void list( final PrintStream out ) {
		parent.list( out );
	}

	@Override
	public void list( final PrintWriter out ) {
		parent.list( out );
	}

}
