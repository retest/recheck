package de.retest.recheck.review.counter;

public class NopCounter implements Counter {

	private static final Counter INSTANCE = new NopCounter();

	public static Counter getInstance() {
		return INSTANCE;
	}

	private NopCounter() {}

	@Override
	public void add() {
		// do nothing
	}

	@Override
	public void remove() {
		// do nothing
	}
}
