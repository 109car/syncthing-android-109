package com.nutomic.syncthingandroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nutomic.syncthingandroid.service.Constants;
import com.nutomic.syncthingandroid.service.SyncthingService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        Log.i(TAG, "onReceive action=" + action);

        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            Log.w(TAG, "ignored broadcast");
            return;
        }

        if (!startServiceOnBoot(context)) {
            Log.i(TAG, "startServiceOnBoot=false");
            return;
        }

        Log.i(TAG, "starting service from boot/package-replaced");
        startServiceCompat(context);
    }

    /**
     * Workaround for starting service from background on Android 8+.
     */
    public static void startServiceCompat(Context context) {
        Log.i(TAG, "startServiceCompat");
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(appContext, SyncthingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent);
        } else {
            appContext.startService(intent);
        }
    }

    static boolean startServiceOnBoot(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.PREF_START_SERVICE_ON_BOOT, false);
    }
}