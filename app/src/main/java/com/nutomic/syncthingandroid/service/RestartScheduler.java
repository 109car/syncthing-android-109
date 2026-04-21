package com.nutomic.syncthingandroid.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.nutomic.syncthingandroid.receiver.RestartReceiver;

public final class RestartScheduler {

    private static final String TAG = "RestartScheduler";
    private static final int REQUEST_CODE_RESTART = 10901;

    private RestartScheduler() {
    }

    public static void scheduleRestart(Context context, long delayMs) {
        Context appContext = context.getApplicationContext();
        AlarmManager alarmManager =
                (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        long triggerAtMillis = System.currentTimeMillis() + Math.max(3000L, delayMs);
        PendingIntent restartPi = getRestartPendingIntent(appContext);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        restartPi
                );
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        restartPi
                );
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        restartPi
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        restartPi
                );
            }
        } catch (SecurityException e) {
            Log.w(TAG, "Exact alarm denied, falling back to inexact alarm.", e);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        restartPi
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        restartPi
                );
            }
        }
    }

    public static void cancelRestart(Context context) {
        Context appContext = context.getApplicationContext();
        AlarmManager alarmManager =
                (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.w(TAG, "cancelRestart skipped: alarmManager == null");
            return;
        }

        Log.i(TAG, "cancelRestart");
        alarmManager.cancel(getRestartPendingIntent(appContext));
    }

    private static PendingIntent getRestartPendingIntent(Context context) {
        Intent intent = new Intent(context, RestartReceiver.class)
                .setAction(RestartReceiver.ACTION_RESTART_FROM_GUARD)
                .setPackage(context.getPackageName());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_RESTART,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | Constants.FLAG_IMMUTABLE
        );
    }
}
