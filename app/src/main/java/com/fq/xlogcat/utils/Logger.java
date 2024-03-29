package com.fq.xlogcat.utils;

import java.util.Arrays;

import android.util.Log;

/**
 * Easier way to interact with logcat.
 * @author nolan
 */
public class Logger {

	public static final boolean DEBUG_MODE = true;
	
	private String tag;
	
	public Logger(String tag) {
		this.tag = tag;
	}
	
	public Logger(Class<?> clazz) {
		this.tag = clazz.getSimpleName();
	}
	
	public void i(String format, Object... more) {
		Log.i(tag, String.format(format, more));
	}
	
	public void i(Exception e, String format, Object... more) {
		Log.i(tag, String.format(format, more), e);
	}
	
	public void w(Exception e, String format, Object... more) {
		Log.w(tag, String.format(format, more), e);
	}
	
	public void w(String format, Object... more) {
		Log.w(tag, String.format(format, more));
	}	
	
	public void e(String format, Object... more) {
		Log.e(tag, String.format(format, more));
	}	
	
	public void e(Exception e, String format, Object... more) {
		Log.e(tag, String.format(format, more), e);
	}
	
	public void d(String format, Object... more) {	
		if (DEBUG_MODE) {
			for (int i = 0; i < more.length; i++) {
				if (more[i] instanceof int[]) {
					more[i] = Arrays.toString((int[])more[i]);
				}
			}
			Log.d(tag, String.format(format, more));
		}
	}	
	
	public void d(Exception e, String format, Object... more) {
		if (DEBUG_MODE) {
			for (int i = 0; i < more.length; i++) {
				if (more[i] instanceof int[]) {
					more[i] = Arrays.toString((int[])more[i]);
				}
			}
			Log.d(tag, String.format(format, more), e);
		}
	}	
}
