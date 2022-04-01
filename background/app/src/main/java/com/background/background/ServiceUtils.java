package com.background.background;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


import timber.log.Timber;

public class ServiceUtils {
    public static boolean isRunCalibration = false;
    static int INTERVAL_CHECK_SERVICE_IN_SECOND = 3;
    /**
     * Check service is down and restart them
     */
    static Disposable checkServiceIsRunningScheduler;

    /**
     * Use to stop the current running service
     **/
    public static void stopCurrentService() {
        MyApplication.Companion.applicationContext().stopService(
                new Intent(MyApplication.Companion.applicationContext(),
                        BaseBackgroundService.class));
    }

    /**
     * Use to start the service (PersonCountService, HandDetectionService)
     * isSetting
     *
     * @param context   Context
     * @param isSetting this one indicate SettingActivity is opening or not
     */
    private static void startExtensionService(Context context, boolean isSetting) {
        Intent intent = new Intent(context, BaseBackgroundService.class);
        intent.setAction(BaseBackgroundService.ACTION_START_SERVICE);
        intent.putExtra(BaseBackgroundService.EXTRA_IS_SETTING, isSetting);
        ContextCompat.startForegroundService(context, intent);
    }

    /**
     * Auto start media for every {@value INTERVAL_CHECK_SERVICE_IN_SECOND} second
     *
     * @param context Context
     */
    public static void autoStartMedia(Context context) {
        Log.d("###","autoStartMedia");
        checkServiceIsRunningScheduler = Observable.interval(INTERVAL_CHECK_SERVICE_IN_SECOND, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                    //Auto start service
                    if (!isMyServiceRunning(BaseBackgroundService.class,
                            MyApplication.Companion.applicationContext())) {
                        Log.d("###","startExtensionService");
                        ServiceUtils.startExtensionService(context, false);
                    }

                   // Log.d("###","AppController.getInstance().isInputFrameRunning():"+AppController.getInstance().isInputFrameRunning());

                    /**
                     * Autostart input frame
                     */
                   // AppController.getInstance().startProcessingImage();
                    if (!AppController.getInstance().isInputFrameRunning()) {
                      //  Log.d("###","bbbb");

                    }
                });
    }

    private static boolean isPermissionGrant(Context context) {
        return PermissionUtils.hasPermissions(context);
    }

    private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        Timber.i("Service not running");
        return false;
    }
}
