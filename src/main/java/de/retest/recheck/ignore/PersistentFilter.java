package de.retest.recheck.ignore;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString( onlyExplicitlyIncluded = true )
@AllArgsConstructor
public class PersistentFilter implements Filter {

	@Getter
	private final Path path;

	@ToString.Include
	@Getter
	// Delegate
	private final Filter filter;

	public static Stream<PersistentFilter> wrap( final Path path, final Stream<Filter> filter ) {
		return filter.map( f -> new PersistentFilter( path, f ) );
	}

	public static List<Filter> unwrap( final List<? extends Filter> input ) {
		return input.stream().map( f -> f instanceof PersistentFilter ? ((PersistentFilter) f).getFilter() : f )
				.collect( Collectors.toList() );
	}

	@Override
	public boolean matches( final Element element ) {
		// Delegate
		return filter.matches( element );
	}

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		// Delegate
		return filter.matches( element, attributeKey );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		// Delegate
		return filter.matches( element, attributeDifference );
	}

}
