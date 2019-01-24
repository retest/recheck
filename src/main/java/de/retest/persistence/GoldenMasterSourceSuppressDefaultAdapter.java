package de.retest.persistence;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.retest.ui.review.GoldenMasterSource;

public class GoldenMasterSourceSuppressDefaultAdapter extends XmlAdapter<GoldenMasterSource, GoldenMasterSource> {

	@Override
	public GoldenMasterSource unmarshal( final GoldenMasterSource source ) throws Exception {
		if ( source == null ) {
			return GoldenMasterSource.RECORDED;
		}
		return source;
	}

	@Override
	public GoldenMasterSource marshal( final GoldenMasterSource source ) throws Exception {
		if ( source == GoldenMasterSource.RECORDED ) {
			return null;
		}
		return source;
	}
}
