package de.retest.recheck.review;

public interface HasUnsafedChangesListener {

	void nowHasUnsafedChanges();

	void nowAllChangesSafed();

	public static final HasUnsafedChangesListener NOOP_LISTENER = new HasUnsafedChangesListener() {

		@Override
		public void nowHasUnsafedChanges() {}

		@Override
		public void nowAllChangesSafed() {}

	};
}
