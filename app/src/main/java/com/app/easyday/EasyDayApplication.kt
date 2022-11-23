package com.app.easyday

import android.content.ContextWrapper
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.multidex.MultiDexApplication
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler

import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class EasyDayApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        registerReceiver(
            TransferNetworkLossHandler.getInstance(applicationContext), IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
            )
        )

    }
}