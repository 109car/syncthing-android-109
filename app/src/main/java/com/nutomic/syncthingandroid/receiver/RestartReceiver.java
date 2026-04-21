package com.nutomic.syncthingandroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nutomic.syncthingandroid.service.GuardWork;

public class RestartReceiver extends BroadcastReceiver {

    private static final String TAG = "RestartReceiver";

    public static final String ACTION_RESTART_FROM_GUARD =
            "com.nutomic.syncthingandroid.action.RESTART_FROM_GUARD";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        Log.i(TAG, "onReceive action=" + action);

        if (intent == null || !ACTION_RESTART_FROM_GUARD.equals(action)) {
            Log.w(TAG, "ignored broadcast");
            return;
        }

        Log.i(TAG, "starting service from guard receiver");
        BootReceiver.startServiceCompat(context);
    }
}