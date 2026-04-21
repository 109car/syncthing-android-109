package com.nutomic.syncthingandroid.service;

import android.content.Context;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;

public final class GuardWork {

    private static final String UNIQUE_WORK_NAME = "KeepSyncthingAliveImmediate";

    private GuardWork() {
    }

    public static void enqueueImmediateCheck(Context context) {
        Context appContext = context.getApplicationContext();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(KeepAliveWorker.class)
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .addTag(UNIQUE_WORK_NAME)
                        .build();

        WorkManager.getInstance(appContext).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
        );
    }
}
