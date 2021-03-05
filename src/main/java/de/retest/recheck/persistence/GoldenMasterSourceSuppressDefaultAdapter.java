package de.retest.recheck.persistence;

import de.retest.recheck.ui.review.GoldenMasterSource;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
