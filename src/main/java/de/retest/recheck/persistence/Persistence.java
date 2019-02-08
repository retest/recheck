package de.retest.recheck.persistence;

import java.io.IOException;
import java.net.URI;

public interface Persistence<T extends Persistable> {

	/**
	 * @param identifier
	 *            URI on which it should be persisted
	 * @param element
	 *            element to be persisted
	 * @throws IOException
	 *             if an {@code IOException} occurs
	 */
	void save( URI identifier, T element ) throws IOException;

	/**
	 * @param identifier
	 *            URI on which it should be persisted
	 * @return loaded object, otherwise null
	 * @throws IOException
	 *             if an {@code IOException} occurs
	 */
	T load( URI identifier ) throws IOException;

}
