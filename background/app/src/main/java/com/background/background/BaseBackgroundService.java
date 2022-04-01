package com.background.background;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

import timber.log.Timber;

public class BaseBackgroundService extends LifecycleService {
    public static final String CHANNEL_ID = "AWL Lite Service";
    public static final String ACTION_START_SERVICE = "start AWL Lite G2 service";
    public static final String ACTION_STOP_SERVICE = "stop AWL Lite G2 service";
    public static final String EXTRA_IS_SETTING = "extra_is_setting_mode";
    public static final String SERVICE_RESTART = "awl.service.restart";
    public static final String SERVICE_STANDBY_MODE = "awl.service.standby";
    public static final String SERVICE_RE_CREATE_ACTIVITY = "awl.service.recreate.activity";
    public static final String TAG = BaseBackgroundService.class.getSimpleName();
    private static final int FOREGROUND_ID = 1;

    public CompositeDisposable disposable;

    protected boolean isSettingMode;
    private boolean isServiceStarted = false;
    private BroadcastReceiver mNetworkReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createNotificationChannel(R.string.act_base_services_toast_channel);
        startForeground(FOREGROUND_ID, notification);
        Log.d("###","onCretae basebackgroundService");
        AppController.getInstance().setLifeCycleOwnerForCamera(this).andThen(AppController.getInstance().startProcessingImage()).subscribe();
       /* if (!isServiceStarted) {
            isServiceStarted = true;
            //Listener network connected or not.
           // startListenerFirebase();
        }*/
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            if (Objects.equals(intent.getAction(), ACTION_START_SERVICE)) {

                keepPowerAlive(intent);
            }
            if (Objects.equals(intent.getAction(), ACTION_STOP_SERVICE)) {

                stopAWLiteService(startId);
            }
            return START_STICKY;
        }

        return START_STICKY;
    }

    /**
     * start listener firebase. interval time to check device id when empty
     */


    private void stopAWLiteService(int startId) {
        Log.d("###","stopAWL");
        //Timber.i(this.getClass().getName()+" stopAWLiteService()");
        stopForeground(true);
        stopSelfResult(startId);
    }

    @SuppressLint("WakelockTimeout")
    private void keepPowerAlive(@NotNull Intent intent) {
        Timber.i(this.getClass().getName()+" keepPowerAlive()");
        if (isServiceStarted) {
            Timber.i("### Service already run, skipp...");
            return;
        }
        //MEMO: Get information from Intent
        isSettingMode = intent.getBooleanExtra(EXTRA_IS_SETTING, false);
        Timber.i("### Start running AWL service");
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AWLEndlessService::lock");
        wakeLock.acquire();;
    }

    private Notification createNotificationChannel(int resString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel serviceChanel = new NotificationChannel(CHANNEL_ID, getResources()
                    .getString(R.string.act_base_services_toast_channel), NotificationManager.IMPORTANCE_HIGH);
            serviceChanel.setDescription(getResources()
                    .getString(resString));
            serviceChanel.enableVibration(true);
            notificationManager.createNotificationChannel(serviceChanel);
        }

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        return builder.setContentTitle(getResources()
                .getString(R.string.act_base_services_toast_AWL_lite_service))
                .setContentText(getResources().getString(R.string.act_base_services_toast_camera_running))
                .setPriority(Notification.PRIORITY_HIGH).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i(this.getClass().getName()+" onDestroy()");
        if (mNetworkReceiver != null && mNetworkReceiver.isInitialStickyBroadcast()) {
            unregisterReceiver(mNetworkReceiver);
        }


        Timber.i("onDestroy service");
        isServiceStarted = false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}