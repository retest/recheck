package de.retest.recheck.review.counter;

import java.util.function.IntConsumer;

public class IntCounter implements Counter {

	private int count = 0;

	private final IntConsumer listener;

	public IntCounter( final IntConsumer listener ) {
		this.listener = listener;
		this.listener.accept( count );
	}

	@Override
	public void add() {
		count += 1;
		listener.accept( count );
	}

	@Override
	public void remove() {
		count -= 1;
		listener.accept( count );
	}
}
