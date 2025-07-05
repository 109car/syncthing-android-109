package com.nutomic.syncthingandroid.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class KeepAliveWorker extends Worker {

    public KeepAliveWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!isServiceRunning(getApplicationContext(), SyncthingService.class)) {
            Intent intent = new Intent(getApplicationContext(), SyncthingService.class);
            getApplicationContext().startForegroundService(intent);
        }
        return Result.success();
    }

    private boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}