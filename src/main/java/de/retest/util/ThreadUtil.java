package de.retest.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.SettableFuture;

public class ThreadUtil {

	public static String getShortInfo( final ThreadInfo threadInfo ) {
		return "'" + threadInfo.getThreadName() + "' (" + threadInfo.getThreadState() + ") in method '"
				+ getBottomStack( threadInfo ) + "'";
	}

	public static String getBottomStack( final ThreadInfo threadInfo ) {
		if ( threadInfo.getStackTrace().length > 0 ) {
			return threadInfo.getStackTrace()[0].toString();
		}
		return "";
	}

	public static String getShortInfo( final List<ThreadInfo> activeThreads ) {
		String result = "";
		if ( activeThreads.isEmpty() ) {
			return result;
		}
		for ( final ThreadInfo threadInfo : activeThreads ) {
			result += getShortInfo( threadInfo ) + ", ";
		}
		return result.substring( 0, result.length() - 2 );
	}

	public static boolean isDaemon( final ThreadInfo threadInfo ) {
		for ( final Thread thread : getAllThreads() ) {
			if ( thread.getId() == threadInfo.getThreadId() ) {
				return thread.isDaemon();
			}
		}
		return false;
	}

	public static ThreadInfo[] getAllThreadInfos() {
		return ManagementFactory.getThreadMXBean().dumpAllThreads( true, true );
	}

	public static ThreadGroup getRootThreadGroup() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup parent = group.getParent();
		while ( parent != null ) {
			group = parent;
			parent = group.getParent();
		}
		return group;
	}

	public static Thread[] getAllThreads() {
		final ThreadGroup root = getRootThreadGroup();
		final Thread[] threads = new Thread[root.activeCount() * 2];
		final int n = root.enumerate( threads, true );
		return java.util.Arrays.copyOf( threads, n );
	}

	public static void sleep( final long ms ) {
		final long start = System.currentTimeMillis();
		while ( System.currentTimeMillis() - start < ms ) {
			try {
				Thread.sleep( ms );
			} catch ( final InterruptedException exc ) {
				Thread.interrupted();
			}
		}
	}

	public static StackTraceElement[] getStackTraceFor( final ThreadInfo threadInfo ) {
		final Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
		for ( final Map.Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet() ) {
			if ( entry.getKey().getId() == threadInfo.getThreadId() ) {
				return entry.getValue();
			}
		}
		return null;
	}

	public static boolean tookLongerThan( final long start, final int i ) {
		final long now = System.currentTimeMillis();
		return (now - start) / 1000 > i;
	}

	public static <T> T getFromFuture( final SettableFuture<T> future, final int seconds ) {
		final long start = System.currentTimeMillis();
		while ( !tookLongerThan( start, seconds ) ) {
			try {
				return future.get( seconds, TimeUnit.SECONDS );
			} catch ( final ExecutionException exc ) {
				throw new RuntimeException( exc.getCause() );
			} catch ( final TimeoutException exc ) {
				throw new RuntimeException( "Timed out while waiting for future.", exc );
			} catch ( final InterruptedException exc ) {
				Thread.currentThread().interrupt();
			}
		}
		throw new IllegalStateException( "Unreachable code!" );
	}

	public static String stackTraceToString( final StackTraceElement[] stackTrace ) {
		String result = "";
		for ( final StackTraceElement stackTraceElement : stackTrace ) {
			result += "\n\tat " + stackTraceElement.toString();
		}
		return result;
	}

	public static boolean stackTraceContainsClass( final String classInCurrentThread ) {
		for ( final StackTraceElement ste : Thread.currentThread().getStackTrace() ) {
			if ( ste.getClassName().equals( classInCurrentThread ) ) {
				return true;
			}
		}
		return false;
	}
}
