package de.retest.recheck.persistence;

import java.io.Serializable;

public abstract class Persistable implements Serializable {

	private static final long serialVersionUID = 1L;
	private final int persistenceVersion;

	public Persistable( final int persistenceVersion ) {
		this.persistenceVersion = persistenceVersion;
	}

	public int version() {
		return persistenceVersion;
	}
}
