package com.background.background;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.background.background.media.InputFrame;
import com.background.background.media.InputFrameImpl;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;


import timber.log.Timber;


/**
 * Control everything betweem UI and component. Navigate all request from UI and give them to component for processing
 */
public class AppController {
    private static final int HEALTH_CHECK_INTERVAL_SECOND = 10;
    private static final int LOG_INFO_EXTENSION_INTERVAL_MINUS = 1;
    private static final int DELAY_DOWNLOAD_APK_ON_ERROR = 3;
    private static final int MAX_TIME_AUTO_RESTART = 3;
    private static AppController appControllerInstance = null;
    private CompositeDisposable disposable;

    // This var manage Pref Store. Never change this become public or send object to another Component

    // This one manage InputFrameImpl. Never change this become public or send object to another Component
    private InputFrameImpl inputFrame;
    // This var manage API client. Never change this become public or send object to another Component

    private boolean isNetworkAvailable;
    private Disposable rebootForInferenceDisposable;
    private final static int SEND_RESULT_INTERVAL = 5;
    private final static int MAX_FACE_SEND = 50;
    private final static int OFFSET = 0;
    private final static int AUTO_RESTART_TIME = 30 * 60 * 1000;
    private static final String INPUT_FILE = "/local_settings.txt";
    private static final String VIDEO_TYPE = "video";

    private boolean isApkDownloading = false;
    private Disposable sendInferenceResultDispose;
    private PublishSubject<String> awliteEventSubject = PublishSubject.create();

    public float lastedFPS = 0.0f;
    public int lastedCPU = 0;
    public int lastedMemory = 0;


    private long lastDataTime = 0;
    public long lastTelemetrySuccess = 0l;


    /**
     * Initialization class UploadUtils
     *
     * @return uploadUtils UploadUtils
     * <p>
     * <p>
     * /**
     * Private constructor AppController.
     */
    private AppController() {
        try {
           Log.d("###"," AppController");
            initInputFrame();
            Completable.create(emitter -> {
                emitter.onComplete();
            }).subscribeOn(Schedulers.io()).subscribe();
        } catch (RuntimeException e) {
            Timber.e("AppController -> %s", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Initialization class UploadUtils
     */


    /**
     * Create the Observer interval 5 minutes to send the result data.
     */


    /**
     * Generate the result json string for uploading to cloud.
     *
     * @return String
     */


    /**
     * Get data send cloud from database
     *
     * @return List of FaceDetectedData
     */


    private void stopInferenceDisposable() {
        if (rebootForInferenceDisposable != null) {
            rebootForInferenceDisposable.dispose();
            rebootForInferenceDisposable = null;
        }
    }

    private String removeLastChar(String s) {
        return s.substring(0, s.length() - 1);
    }

    /**
     * Initial the control of the application.
     * 1. Check the OperatingHour for interval
     * 2. Check the condition is auto restart Inference.
     */
    private void initAppController() {
       /* Timber.i(this.getClass().getName()+" initAppController()");
        apiManager.createDisposableToServer();
        disposable = new CompositeDisposable();
        // If auto restart enable, then auto restart the inference after minute
        if (settingGateway.loadAutoReloadInterval() > 0) {
            Timber.i("Add auto restart camera interval");
            disposable.add(generalAutoRestart(settingGateway.loadAutoReloadInterval(), settingGateway.isAutoReloadCondition()));
        }
        createSendInferenceResult();*/
    }

    /**
     * Save the PersonCount Result to DB
     *
     * @param personCountResult PersonCountResult
     */


    /**
     * Clear data in Database
     */


    //Only call this from Framework, UI, Service
    public static AppController getInstance() {

        if (appControllerInstance == null)
            appControllerInstance = new AppController();
        return appControllerInstance;
    }


    /**
     * Init api client connect to cloud.
     */


    public void isNextWorkAvailable(boolean isNetworkAvailable) {
        this.isNetworkAvailable = isNetworkAvailable;
    }

    /**
     * Get is network available in application
     *
     * @return isNetworkAvailable boolean.
     */
    public boolean getIsNetWorkAvailable() {
        return isNetworkAvailable;
    }

    /**
     * Init input frame
     */
    private void initInputFrame() {
        inputFrame = new InputFrame();
    }

    /**
     * Get the api Client
     *
     * @return API client.
     */


    /**
     * Must call this one after Init InputFrame
     */


    /**
     * Create the ExtensionInfo from the defaultExtension/
     *
     * @return ExtensionInfo.
     */
  /*  private ExtensionInfo createExtensionInfo() {
        switch (settingGateway.getDefaultExtension()) {
            case PERSON_COUNT:
                return PersonCountExtensionInfo.createExtension((PersonCountSettingInput) settingGateway
                        .loadPersonCountSettingInput(settingGateway.getCameraSelectId(),
                                MyApplication.Companion.applicationContext().getDisplaySize()));
            case HAND_DETECTION:
                return HandDetectionExtensionInfo.createExtension((HandDetectionSettingInput) settingGateway.loadHandDetectionSettingInput(settingGateway.getCameraSelectId(), MyApplication.Companion.applicationContext().getDisplaySize()));
        }
        return null;
    }*/

    /**
     * Receiving the processed DrawUtils from the FrameManager
     *
     * @return PublishSubject DrawUtils
     */


    /**
     * Init inputFrame, only call this one in Service
     */
    public Completable startProcessingImage() {
        return startInputFrame().andThen(Completable.create(emitter -> {
            //Camera start when settingGateway.getIsStandbyMode() is false or isNeedCameraInSetting is true
            //boolean isStandbyMode = AppController.getInstance().getSettingGateway().getIsStandbyMode();
            //True if camera is force to start
//            Log.d("###","startProcessingImage");
//            try {
//                startFrameManager();
//            } catch (Exception e) {
//                Timber.e("### Error in AppController:%s", e.getMessage());
//            }
//
//            //restart interval
//            if (disposable == null) {
//                initAppController();
//            }
            emitter.onComplete();
        }).subscribeOn(AndroidSchedulers.mainThread()));
    }

    public Completable startInputFrame() {
        if (inputFrame == null) {
        }
        return inputFrame.startCamera(0);
    }

    /**
     * Start upload file zip LocalLog
     *
     * @param deviceLog      DeviceLogModel
     * @param callBackUpdate UploadUtils.EventUpLocalLog
     */


    /**
     * Stop stream
     */


    private void startFrameManager() {
       /* if (frameManager != null) {
            Timber.i("Start FrameManager");
            frameManager.initialExtensionInfo(createExtensionInfo());
        }*/
    }

    public BehaviorSubject<Boolean> standByMode = BehaviorSubject.create();
    public BehaviorSubject<Boolean> isShowSystemLog = BehaviorSubject.create();

    public void stopMediaInput() {
        inputFrame.stopFrame().subscribe();
    }

    public void stopInputFrame() {
        Timber.d("### stopInputFrame");
        // AppController.getInstance().getSettingGateway().saveStatusKitting(false);
        // Timber.e("Stop Camera");
        Timber.e(this.getClass().getName() + " stopInputFrame");

        stopMediaInput();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    public List<Size> getSupportedCameraResolutions() {
        return inputFrame.getSupportedCameraResolutions();
    }

    public PublishSubject<Bitmap> getFrameBitmap() {
        return inputFrame.getBitmap();
    }

    public boolean isInputFrameRunning() {
        return inputFrame != null && inputFrame.isMediaRunning();
    }

    public Completable setLifeCycleOwnerForCamera(LifecycleOwner lifecycleOwner) {
        Log.d("###", "setLifeCycleOwnerForCamera");
        return inputFrame.setLifeCycle(lifecycleOwner);
    }

    /**
     * get desired size of bitmap
     *
     * @return Size
     */
    public Size getDesiredAnalysisSize() {
        //Collect from camera first
        Size desiredSize = inputFrame.getActualCameraResolution();

        //If dont have. Read from Preference

        return desiredSize;
    }

    /**
     * Save userId and accessKey into camera pref
     *
     * @param userId    String UserID
     * @param accessKey String accessKey
     */

    /**
     * Create the Observable for the debugging information.
     * This observable will fire at 500 ms for the current status of the Inference
     * CPU usage, Memory usage, Internet status, FPS value, Extension running and Camera input size.
     *
     * @return Observale String
     */


    /**
     * Auto restart for general
     *
     * @param reloadInterval interval Auto Restart for every minutes
     * @param faceCondition  If not detect any face. Reboot will execute
     * @return Disposable
     */


    /**
     * Set even show system show log
     */
    public void setEvenShowSystemLog(boolean isShow) {
        if (isShowSystemLog != null) {
            isShowSystemLog.onNext(isShow);
        }
    }


    /**
     * Send network level to firebase
     *
     * @param request Object contain network_level
     * @return Completable
     */


    /**
     * Delete all pref store in device
     *
     * @return Completable
     */


    public Observable<String> awliteEvent() {
        return awliteEventSubject;
    }
}
