package com.background.background.media;

import android.graphics.Bitmap;
import android.util.Size;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;


public interface InputFrameImpl {
    /**
     * Get resolution of camera
     * @return
     */
    List<Size> getSupportedCameraResolutions();

    /**
     * Get bitmap data
     * @return Bitmap
     */
    public PublishSubject<Bitmap> getBitmap();

    /**
     * Get stream data
     * @return StreamInputData
     */


    /**
     * Stop get data from InputFrame
     */
    Completable  stopFrame();

    Completable startCamera( int screenRotation);

    Completable startCamera(String path, int screenRotation);

    /**
     * Check input frame is running
     * @return
     */
    boolean isMediaRunning();

    /**
     * Camera need this
     */
    Completable setLifeCycle(LifecycleOwner lifecycleOwner);

    /**
     * get actual camera resolution
     *
     * For some reason, camera will return different size. Apply for camera resolution
     * @return Size if existing. Otherwise return null
     */
     Size getActualCameraResolution();
}
