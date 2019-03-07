package de.retest.recheck.ui;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class PathAdapter extends XmlAdapter<String, Path> {

	@Override
	public Path unmarshal( final String string ) throws Exception {
		return Path.fromString( string );
	}

	@Override
	public String marshal( final Path path ) throws Exception {
		return path.toString();
	}

}
