package com.nutomic.syncthingandroid.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.nutomic.syncthingandroid.R;

public final class GuardNotifier {

    private static final String CHANNEL_ID = "04_syncthing_guard";
    private static final int ID_ACCESSIBILITY_DISABLED = 10912;

    private GuardNotifier() {
    }

    public static void showAccessibilityDisabledNotification(Context context) {
        Context appContext = context.getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Syncthing-109 后台守护",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(false);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        Intent settingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                appContext,
                ID_ACCESSIBILITY_DISABLED,
                settingsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | Constants.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle("Syncthing-109 无障碍守护已关闭")
                .setContentText("点此重新打开无障碍守护，提升后台运行稳定性。")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "系统刚刚断开了 Syncthing-109 的无障碍守护。点击本通知进入无障碍设置页，"
                                + "重新启用后，应用会立即尝试恢复后台守护。"
                ))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(ID_ACCESSIBILITY_DISABLED, builder.build());
    }

    public static void cancelAccessibilityDisabledNotification(Context context) {
        Context appContext = context.getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(ID_ACCESSIBILITY_DISABLED);
        }
    }
}
