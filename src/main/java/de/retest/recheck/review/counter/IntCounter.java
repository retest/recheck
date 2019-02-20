package de.retest.recheck.review.counter;

import de.retest.recheck.review.HasUnsafedChangesListener;

public class IntCounter implements Counter {

	private int count = 0;

	private final HasUnsafedChangesListener listener;

	public IntCounter( final HasUnsafedChangesListener listener ) {
		this.listener = listener;
		this.listener.nowHasUnsafedChanges();
	}

	@Override
	public void add() {
		count += 1;
		listener.nowHasUnsafedChanges();
	}

	@Override
	public void remove() {
		count -= 1;
		listener.nowHasUnsafedChanges();
	}

	@Override
	public int getCount() {
		return count;
	}

}
