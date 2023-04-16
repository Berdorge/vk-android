package com.happy.easter

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.happy.easter.utils.stackTraceFeature
import java.util.concurrent.CopyOnWriteArrayList

class HappyEasterApplication(
    private val context: Context
) {
    private var binder: HappyEasterServiceInterface? = null
    private val features = CopyOnWriteArrayList<Feature>()

    init {
        val intent = Intent(context, HappyEasterService::class.java)
        context.startService(intent)
        context.bindService(
            intent,
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    binder = HappyEasterServiceInterface.Stub.asInterface(service)
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    binder = null
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }

    internal fun addFeature(urlRegex: Regex, feature: String) {
        features.add(Feature(urlRegex, feature))
    }

    internal fun removeFeature(urlRegex: Regex) {
        for (feature in features) {
            if (feature.urlRegex == urlRegex) {
                features.remove(feature)
            }
        }
    }

    internal fun getFeature(url: String) = features.lastOrNull {
        it.urlRegex.matches(url)
    }
        ?.label
        ?: stackTraceFeature()

    internal fun getBinder() = checkNotNull(binder)

    internal fun getVersionName(): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: HappyEasterApplication? = null

        /**
         * Initialize the HappyEasterApplication with the application context.
         *
         * In a process, other than the main process, this method must be called
         * before using any other HappyEaster APIs.
         *
         * @param context The application context.
         */
        fun init(context: Context) {
            instance = HappyEasterApplication(context)
        }

        internal fun isInitialized() = instance != null

        internal fun getInstance() = checkNotNull(instance) {
            "HappyEasterApplication is not initialized"
        }
    }

    private data class Feature(
        val urlRegex: Regex,
        val label: String
    )
}
