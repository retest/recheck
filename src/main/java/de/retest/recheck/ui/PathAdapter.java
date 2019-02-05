package de.retest.recheck.ui;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PathAdapter extends XmlAdapter<Path, Path> {

	@Override
	public Path marshal( final Path value ) throws Exception {
		return Path.path( new PathElement( value.toString() ) );
	}

	@Override
	public Path unmarshal( final Path value ) throws Exception {
		return Path.fromString( value.toString() );
	}

}
