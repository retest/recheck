package de.retest.recheck.ignore;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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

	public static List<Filter> unwrap( final List<Filter> input ) {
		return input.stream().map( f -> f instanceof PersistentFilter ? ((PersistentFilter) f).getFilter() : f ).collect( Collectors.toList() );
	}
}
