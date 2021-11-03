package de.retest.recheck.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.List;

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

	public static ThreadInfo[] getAllThreadInfos() {
		return ManagementFactory.getThreadMXBean().dumpAllThreads( true, true );
	}

	public static String stackTraceToString( final StackTraceElement[] stackTrace ) {
		final StringBuilder result = new StringBuilder();
		for ( final StackTraceElement stackTraceElement : stackTrace ) {
			result.append( "\n\tat " ).append( stackTraceElement.toString() );
		}
		return result.toString();
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
