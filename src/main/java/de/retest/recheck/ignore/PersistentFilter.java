package de.retest.recheck.ignore;

import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;

@ToString
@AllArgsConstructor
public class PersistentFilter implements Filter {

	@Getter
	private final Path path;

	@ToString.Include
	@Delegate
	@Getter
	private final Filter filter;

}
