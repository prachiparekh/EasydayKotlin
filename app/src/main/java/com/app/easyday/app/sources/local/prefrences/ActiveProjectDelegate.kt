package com.app.easyday.app.sources.local.prefrences

import com.pixplicity.easyprefs.library.Prefs
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ActiveProjectDelegate : ReadWriteProperty<AppPreferencesDelegates, Int> {

    companion object {
        const val PREF_KEY_APP_RUN_COUNT = "active_project"
        const val run_count = 0
    }

    override fun getValue(thisRef: AppPreferencesDelegates, property: KProperty<*>): Int =
        Prefs.getInt(PREF_KEY_APP_RUN_COUNT, run_count)


    override fun setValue(
        thisRef: AppPreferencesDelegates,
        property: KProperty<*>,
        value: Int
    ) {
        Prefs.putInt(PREF_KEY_APP_RUN_COUNT, value)
    }

}
