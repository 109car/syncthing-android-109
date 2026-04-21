package com.nutomic.syncthingandroid.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nutomic.syncthingandroid.receiver.BootReceiver;
import com.nutomic.syncthingandroid.util.ConfigXml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class KeepAliveWorker extends Worker {

    private static final String TAG = "KeepAliveWorker";

    public KeepAliveWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        boolean serviceRunning = isServiceRunning(context, SyncthingService.class);

        if (!serviceRunning) {
            Log.w(TAG, "SyncthingService missing, starting foreground service");
            BootReceiver.startServiceCompat(context);
        } 

    return Result.success();
   	}

    private void restartSyncthingService(Context context) {
        Intent intent = new Intent(context, SyncthingService.class)
                .setAction(SyncthingService.ACTION_RESTART);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        GuardWork.enqueueImmediateCheck(context);
    }

    private boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo service
                : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isSyncthingProcessAlive(Context context) {
        File procRoot = new File("/proc");
        File[] entries = procRoot.listFiles();
        if (entries == null) {
            return false;
        }

        String binaryPath = Constants.getSyncthingBinary(context).getAbsolutePath();

        for (File entry : entries) {
            if (!entry.isDirectory() || !TextUtils.isDigitsOnly(entry.getName())) {
                continue;
            }

            File cmdline = new File(entry, "cmdline");
            if (!cmdline.exists()) {
                continue;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(cmdline))) {
                String line = reader.readLine();
                if (line != null
                        && (line.contains(binaryPath)
                        || line.contains(Constants.FILENAME_SYNCTHING_BINARY))) {
                    return true;
                }
            } catch (IOException ignored) {
                // Ignore and continue scanning other /proc entries.
            }
        }

        return false;
    }

    private boolean isWebGuiHealthy(Context context) {
        HttpURLConnection connection = null;
        try {
            ConfigXml config = new ConfigXml(context);
            URL baseUrl = config.getWebGuiUrl();
            String base = baseUrl.toString();
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }

            URL healthUrl = new URL(base + "/rest/noauth/health");
            connection = (HttpURLConnection) healthUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                return false;
            }

            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            String text = body.toString();
            return text.contains("\"status\"") && text.contains("OK");
        } catch (Exception e) {
            Log.w(TAG, "Web GUI health check failed.", e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
