package com.nutomic.syncthingandroid.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.nutomic.syncthingandroid.receiver.BootReceiver;

public class KeepAliveAccessibilityService extends AccessibilityService {

    private static final String TAG = "KeepAliveA11y";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected");

        RestartScheduler.cancelRestart(this);
        GuardNotifier.cancelAccessibilityDisabledNotification(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG, "onUnbind");

        GuardNotifier.showAccessibilityDisabledNotification(this);
        RestartScheduler.scheduleRestart(this, 3000L);

        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No-op on purpose.
    }

    @Override
    public void onInterrupt() {
        // No-op on purpose.
    }
}
