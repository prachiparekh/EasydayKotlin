package com.app.easyday

import android.content.ContextWrapper
import androidx.multidex.MultiDexApplication
import com.onegravity.rteditor.fonts.FontManager
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EasyDayApplication : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        FontManager.preLoadFonts(this)
    }
}