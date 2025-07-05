package com.nutomic.syncthingandroid.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class KeepAliveAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No-op
    }

    @Override
    public void onInterrupt() {
        // No-op
    }
}