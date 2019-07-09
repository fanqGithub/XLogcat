package com.fq.xlogcat.logcat;

import android.os.AsyncTask;
import android.util.Log;

import com.fq.xlogcat.service.LogWindowService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

/**
 * @author fanqi
 * @date 2019-07-09
 * Description:
 */
public class VirtualTerminal extends AsyncTask<Void, String, String> {

    public static final String TAG = VirtualTerminal.class.getSimpleName();
    private static final boolean ISDEBUG = true;

    private WeakReference<LogWindowService> mWeakService;

    public VirtualTerminal(LogWindowService service) {
        mWeakService = new WeakReference<>(service);
    }

    @Override
    protected String doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: ");
        String results = "";
        debug("execute start : adb logcat");
        Process process = null;
        BufferedReader successReader = null;
        BufferedReader errorReader = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec("logcat");
            if (process == null) {
                return null;
            }
            errorMsg = new StringBuilder();
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String lineStr;
            while ((lineStr = successReader.readLine()) != null) {
                results = lineStr;
                publishProgress(lineStr);
            }
            while ((lineStr = errorReader.readLine()) != null) {
                errorMsg.append(lineStr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (successReader != null) {
                    successReader.close();
                }
                if (errorReader != null) {
                    errorReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return results;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute: ");
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (mWeakService.get() != null) {
            mWeakService.get().refreshLog(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: ");
    }

    private static void debug(String message) {
        if (ISDEBUG) {
            Log.d(TAG, message);
        }
    }


}
