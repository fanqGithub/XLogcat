package com.fq.xlogcat.logcat;

import android.os.Build;
import android.util.Log;

import com.fq.xlogcat.BuildConfig;

import java.util.Arrays;

/**
 * Created by fanqi on 2019-07-09.
 * Description:
 */
public class XLog {

    private String tag;


    public XLog(Class<?> clazz) {
        this.tag = clazz.getCanonicalName();
    }

    public void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public void d(String format, Object... more) {
        if (BuildConfig.DEBUG) {
            for (int i = 0; i < more.length; i++) {
                if (more[i] instanceof int[]) {
                    more[i] = Arrays.toString((int[]) more[i]);
                }
            }
            Log.d(tag, String.format(format, more));
        }
    }

    public void d(Exception e, String format, Object... more) {
        if (BuildConfig.DEBUG) {
            for (int i = 0; i < more.length; i++) {
                if (more[i] instanceof int[]) {
                    more[i] = Arrays.toString((int[]) more[i]);
                }
            }
            Log.d(tag, String.format(format, more), e);
        }
    }


}
