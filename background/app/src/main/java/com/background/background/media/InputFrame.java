package com.background.background.media;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Size;

import androidx.lifecycle.LifecycleOwner;

//import com.background.background.camera.CameraManager;

import com.background.background.camera.Camera;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

import timber.log.Timber;

public class InputFrame implements InputFrameImpl {
 //   private CameraManager cameraManager;

    private Size desiredSize;





    @Override
    public List<Size> getSupportedCameraResolutions() {
        //return CameraManager.getListSupportedCameraResolutions();
        return null;
    }

    @Override
    public PublishSubject<Bitmap> getBitmap() {
        return outputData.sendBitmap;
    }



    @Override
    public Completable stopFrame() {
        return Completable.create(emitter -> {
            //depend to which one created. it will stop base on that.
            Timber.i(getClass().getName(),"stopGenerateFromVideo stopFrame");
    /*        if (cameraManager != null) {
                cameraManager.release();
                cameraManager = null;
            }*/

            emitter.onComplete();
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * All data need in Input Frame
     */

    int screenRotation;
    LifecycleOwner lifecycleOwner;

    /**
     * Data will going out
     * Why this one need static. Because when switch to SettingActivity and return back to PersonCountActivity,
     * Service will call AppController.initInputFrame() and create new InputFrame again.
     * At that moment OutputData.sendBitmap will lose and never send any bitmap to PersonCountActivity.
     */
    private static class OutputData {
        static PublishSubject<Bitmap> sendBitmap = PublishSubject.create();

    }

    private OutputData outputData = new OutputData();


    @Override
    public Completable startCamera(int screenRotation) {
        return Completable.create(emitter -> {

            if (lifecycleOwner != null) {
                this.screenRotation = screenRotation;
                Camera camera = new Camera();
                camera.startCamera(lifecycleOwner);
            } else {
                Log.d("###","lifecycleOwner null");
            }
            emitter.onComplete();
        });
    }

    @Override
    public Completable startCamera( String path, int screenRotation) {
        return Completable.create(emitter -> {
            if (lifecycleOwner != null) {
                Log.i("Test","startCamera Input type video and path "+path);
                this.screenRotation = screenRotation;

            } else {
                Timber.e("InputFrame dont have lifecycleOwner");
            }
        });
    }

    @SuppressLint("CheckResult")
    public InputFrame() {
    }

    @Override
    public boolean isMediaRunning() {
        Timber.d("### isMediaRunning");
           // return cameraManager != null;
        return false;
    }

    @Override
    public Completable setLifeCycle(LifecycleOwner lifecycle) {

        return Completable.create(emitter -> {
            Log.d("###","setLifeCycle");
            if(lifecycle !=null){
                Log.d("###","lifecycle !=nul");
            }
            lifecycleOwner = lifecycle;

            emitter.onComplete();
        });
    }

    @Override
    public Size getActualCameraResolution() {
           /* if (isMediaRunning() && cameraManager != null && cameraManager.getBufferBitmap() != null) {
                return new Size(cameraManager.getBufferBitmap().getWidth(), cameraManager.getBufferBitmap().getHeight());
            } else {
                return null;
            }*/
        return null;


    }
}
