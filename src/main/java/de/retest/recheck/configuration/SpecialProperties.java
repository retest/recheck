package de.retest.recheck.configuration;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Supplier;

import de.retest.recheck.Properties;
import de.retest.recheck.util.ThreadUtil;

class SpecialProperties extends PropertiesProxy {

	private static final long serialVersionUID = 1L;

	final static Map<String, Supplier<String>> specials = new HashMap<>();
	static {
		specials.put( Properties.WORK_DIRECTORY, new Supplier<String>() {
			@Override
			public String get() {
				if ( Configuration.isLoaded() ) {
					return Configuration.getRetestWorkspace().toString();
				} else {
					return null;
				}
			}
		} );
		specials.put( Properties.INSTALL_DIRECTORY, new Supplier<String>() {
			@Override
			public String get() {
				if ( !ThreadUtil.stackTraceContainsClass( getClass().getName() ) ) {
					return Properties.getReTestInstallDir().getAbsolutePath();
				} else {
					return null;
				}
			}
		} );
	}

	SpecialProperties( final java.util.Properties parent ) {
		super( parent );

	}

	@Override
	public String getProperty( final String key ) {
		final Supplier<String> handler = specials.get( key );
		if ( handler != null && handler.get() != null ) {
			return handler.get();
		} else {
			return super.getProperty( key );
		}
	}

	@Override
	public String getProperty( final String key, final String defaultValue ) {
		final Supplier<String> handler = specials.get( key );
		if ( handler != null && handler.get() != null ) {
			return handler.get();
		} else {
			return super.getProperty( key, defaultValue );
		}
	}

}
