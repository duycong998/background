package com.background.background;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;


public class PermissionUtils {
    public static final int REQUEST_CODE_PERMISSION = 1;

    public static String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static AlertDialog alertDialog;

    private PermissionUtils() {
        // No instance
    }

    public static boolean hasPermissions(Context context) {
        boolean isCameraGranted =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED;
        boolean isWriteStorageGranted =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
        boolean isLocationGranted =
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        //Timber.i("PermissionUtils isCameraGranted:"+isCameraGranted+" isWriteStorageGranted:"+isWriteStorageGranted+" isLocationGranted:"+isLocationGranted);
        return isCameraGranted && isWriteStorageGranted && isLocationGranted;
    }

    public static boolean shouldShowRequestPermissionsRationale(Activity activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return true;
        }
        for (String permission : permissions) {
            if (!activity.shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }

    public static void showDialogRequestPermission(Activity activity, boolean cancelable) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.request_permission))
                    .setMessage(activity.getString(R.string.you_have_to_give_the_camera_permission_and_memory_to_use_the_app))
                    .setPositiveButton(activity.getString(R.string.yes_go_to_setting), (dialog, w) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivity(intent);
                        dialog.dismiss();
                    })
                    .create();
        }
        alertDialog.setCancelable(cancelable);
        alertDialog.show();
    }
}
