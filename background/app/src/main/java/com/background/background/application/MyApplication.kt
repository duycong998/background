package com.background.background;

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import com.background.background.ServiceUtils
import com.google.gson.Gson
import com.lyft.kronos.KronosClock
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.SocketException

/**
 *
 */
class MyApplication : Application() {
    private var mGSon: Gson? = null
    var displaySize: Point = Point()
        set(point) {
            field = point
        }
    private var rotation = 0

    //KronosClock

    override fun onCreate() {
        super.onCreate()
        instance = this
        //Initial KronosClock

        Timber.d("### onCreate")






        getDisplaySizeVariable()


        //Running check service available. This one alway
        ServiceUtils.autoStartMedia(this)


        // RxJava2 throws UndeliverableException when error occurs after disposable is disposed
        // ref: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
        RxJavaPlugins.setErrorHandler { e: Throwable ->
            var ee: Throwable? = null
            if (e is UndeliverableException) {
                ee = e.cause!!
            }
            if (ee is IOException || ee is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                Timber.w(
                    "RxJavaPlugins: Ignore Exception: $e"
                )
                return@setErrorHandler
            }
            if (ee is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                Timber.w(
                    "RxJavaPlugins: Ignore Exception: $e"
                )
                return@setErrorHandler
            }
            if (ee is NullPointerException || ee is IllegalArgumentException) {
                // that's likely a bug in the application
                Timber.w(
                    "RxJavaPlugins: Forward Exception: $e"
                )
                if (Thread.currentThread().uncaughtExceptionHandler != null) {
                    Thread.currentThread().uncaughtExceptionHandler
                        .uncaughtException(Thread.currentThread(), e)
                }
                return@setErrorHandler
            }
            if (ee is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Timber.w("RxJavaPlugins: Forward Exception: $e")
                if (Thread.currentThread().uncaughtExceptionHandler != null) {
                    Thread.currentThread().uncaughtExceptionHandler
                        .uncaughtException(Thread.currentThread(), e)
                }
                return@setErrorHandler
            }
            Timber.w(
                "Undeliverable exception received, not sure what to do: %s",
                e.localizedMessage
            )
            //Timber.i("Restart Application")
            // Restart the application when there is an exception that causes it to freeze
          //  ImpactTvUtils.restartApplication()
        }

        // Try to catch exception error (Crash)
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Timber.e("Application crash at: %s", t)
            Timber.e(e)
            Timber.i("Restart Application")
            // Restart the application when there is an exception that causes it to freeze

        }
        Timber.i("MyApplication onCreate()")
    }



    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.i("MyApplication onConfigurationChanged()")
        getDisplaySizeVariable()
    }

   /* @SuppressLint("RestrictedApi")
    override fun getCameraXConfig(): CameraXConfig {
        val surfaceManagerProvider = CameraDeviceSurfaceManager.Provider { _, _, _ ->
            RGBSurfaceManager()
        }
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setDeviceSurfaceManagerProvider(surfaceManagerProvider)
            .build()
    }*/

    companion object {
        private const val FIREBASE_CUSTOM_KEY_SN = "SN"
        private var instance: MyApplication? = null
        fun applicationContext(): MyApplication {
            return instance as MyApplication
        }
    }

    /**
     * to getGSON instance
     */
    fun getGSon(): Gson? {
        return mGSon
    }

    fun getScreenRotation(): Int {
        return rotation;
    }

    private fun getDisplaySizeVariable() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        displaySize.x = displayMetrics.widthPixels;
        displaySize.y = displayMetrics.heightPixels
        rotation = windowManager.defaultDisplay.rotation
    }

    fun displayToastMessage(message: String, durationType: Int) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, durationType).show()
        }
    }
}
