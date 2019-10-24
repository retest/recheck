package de.retest.recheck.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class PropertiesListTest {

	PropertiesList propertiesList;
	Properties fist;
	Properties second;
	Properties thrid;

	@Before
	public void setUp() throws Exception {
		fist = new Properties();
		second = new Properties();
		thrid = new Properties();
		propertiesList = new PropertiesList( fist, second, thrid );
	}

	public final void testContainsKeyObject() {
		fist.setProperty( "key1", "value" );
		thrid.setProperty( "key2", "value" );

		final boolean actualKey1 = propertiesList.containsKey( "key1" );
		final boolean actualKey2 = propertiesList.containsKey( "key2" );
		final boolean actualNoKey = propertiesList.containsKey( "noKey" );

		assertThat( actualKey1 ).isTrue();
		assertThat( actualKey2 ).isTrue();
		assertThat( actualNoKey ).isTrue();
	}

	public final void testGetPropertyString() {
		fist.setProperty( "key1", "value1" );
		thrid.setProperty( "key2", "value2" );

		final String actualKey1 = propertiesList.getProperty( "key1" );
		final String actualKey2 = propertiesList.getProperty( "key2" );
		final String actualNoKey = propertiesList.getProperty( "noKey" );

		assertThat( actualKey1 ).isEqualTo( "value1" );
		assertThat( actualKey2 ).isEqualTo( "value2" );
		assertThat( actualNoKey ).isNull();
	}

	public final void testGetPropertyStringString() {
		fist.setProperty( "key", "value" );

		final String actualKey = propertiesList.getProperty( "key", "default" );
		final String actualNoKey = propertiesList.getProperty( "noKey", "default" );

		assertThat( actualKey ).isEqualTo( "value" );
		assertThat( actualNoKey ).isEqualTo( "default" );
	}

	public final void use_correct_order() {
		fist.setProperty( "key1", "value1" );
		second.setProperty( "key1", "value2" );
		thrid.setProperty( "key1", "value3" );
		second.setProperty( "key2", "value2" );
		thrid.setProperty( "key2", "value3" );
		thrid.setProperty( "key3", "value3" );

		final String actualKey1 = propertiesList.getProperty( "key1" );
		final String actualKey2 = propertiesList.getProperty( "key2" );
		final String actualKey3 = propertiesList.getProperty( "key3" );

		assertThat( actualKey1 ).isEqualTo( "value1" );
		assertThat( actualKey2 ).isEqualTo( "value2" );
		assertThat( actualKey3 ).isEqualTo( "value3" );
	}

	// UnsupportedOperations

	@Test( expected = UnsupportedOperationException.class )
	public final void testHashCode() {
		propertiesList.hashCode();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testSize() {
		propertiesList.size();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testIsEmpty() {
		propertiesList.isEmpty();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testRehash() {
		propertiesList.rehash();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testClear() {
		propertiesList.clear();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testSetPropertyStringString() {
		propertiesList.setProperty( null, null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testRemoveObject() {
		propertiesList.remove( null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testContainsObject() {
		propertiesList.contains( null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testContainsValueObject() {
		propertiesList.containsValue( null );
	}

	@Test
	public final void testElements() {
		fist.setProperty( "key1", "v43Ya" );
		second.setProperty( "key1", "ZLQ3t" );
		thrid.setProperty( "key1", "S5ARk" );
		second.setProperty( "key2", "ZLQ3t" );
		thrid.setProperty( "key2", "S5ARk" );
		thrid.setProperty( "key3", "S5ARk" );

		final Enumeration<Object> keys = propertiesList.elements();
		while ( keys.hasMoreElements() ) {
			final Object o = keys.nextElement();
			assertThat( o ).isNotNull();
			assertThat( o ).isIn( Arrays.asList( "S5ARk", "ZLQ3t", "v43Ya" ) );
		}
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testEntrySet() {
		propertiesList.entrySet();
	}

	@Test
	public final void testGetObject() {
		fist.setProperty( "I0QL8", "S5ARk" );
		second.setProperty( "pDaZm", "ZLQ3t" );
		thrid.setProperty( "bppCy", "v43Ya" );

		assertThat( propertiesList.get( "I0QL8" ) ).isEqualTo( "S5ARk" );
		assertThat( propertiesList.get( "pDaZm" ) ).isEqualTo( "ZLQ3t" );
		assertThat( propertiesList.get( "bppCy" ) ).isEqualTo( "v43Ya" );

		assertThat( propertiesList.get( "S5ARk" ) ).isNull();
		assertThat( propertiesList.get( "ZLQ3t" ) ).isNull();
		assertThat( propertiesList.get( "v43Ya" ) ).isNull();
	}

	@Test
	public final void testKeys() {
		fist.setProperty( "I0QL8", "value1" );
		second.setProperty( "I0QL8", "value2" );
		thrid.setProperty( "I0QL8", "value3" );
		second.setProperty( "pDaZm", "value2" );
		thrid.setProperty( "pDaZm", "value3" );
		thrid.setProperty( "bppCy", "value3" );

		final Enumeration<Object> keys = propertiesList.keys();
		while ( keys.hasMoreElements() ) {
			final Object o = keys.nextElement();
			assertThat( o ).isNotNull();
			assertThat( o ).isIn( Arrays.asList( "I0QL8", "pDaZm", "bppCy" ) );
		}
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testKeySet() {
		propertiesList.keySet();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testClone() {
		propertiesList.clone();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testEqualsObject() {
		propertiesList.equals( null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testListPrintStream() {
		propertiesList.list( (PrintStream) null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testListPrintWriter() {
		propertiesList.list( (PrintWriter) null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testLoadInputStream() throws IOException {
		propertiesList.load( (InputStream) null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testLoadReader() throws IOException {
		propertiesList.load( (Reader) null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testLoadFromXMLInputStream() throws Exception {
		propertiesList.loadFromXML( null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testPropertyNames() {
		propertiesList.propertyNames();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testPutObjectObject() {
		propertiesList.put( null, null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testPutAllMapOfQextendsObjectQextendsObject() {
		propertiesList.putAll( null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testSaveOutputStreamString() {
		propertiesList.save( (OutputStream) null, null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testStoreOutputStreamString() throws IOException {
		propertiesList.store( (OutputStream) null, null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testStoreWriterString() throws IOException {
		propertiesList.store( (Writer) null, null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testStoreToXMLOutputStreamString() throws IOException {
		propertiesList.storeToXML( null, null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testStoreToXMLOutputStreamStringString() throws IOException {
		propertiesList.storeToXML( null, null, (String) null );
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testStringPropertyNames() {
		propertiesList.stringPropertyNames();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testToString() {
		propertiesList.toString();
	}

	@Test( expected = UnsupportedOperationException.class )
	public final void testValues() {
		propertiesList.values();
	}

}
