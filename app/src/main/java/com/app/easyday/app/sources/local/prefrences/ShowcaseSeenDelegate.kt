package com.app.easyday.app.sources.local.prefrences

import com.pixplicity.easyprefs.library.Prefs
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ShowcaseSeenDelegate : ReadWriteProperty<AppPreferencesDelegates, Boolean> {

    companion object {
        const val PREF_KEY_SHOWCASE_SEEN = "showcase_seen"
    }

    override fun getValue(thisRef: AppPreferencesDelegates, property: KProperty<*>): Boolean =
        Prefs.getBoolean(PREF_KEY_SHOWCASE_SEEN, false)


    override fun setValue(
        thisRef: AppPreferencesDelegates,
        property: KProperty<*>,
        value: Boolean
    ) {
        Prefs.putBoolean(PREF_KEY_SHOWCASE_SEEN, value)
    }
}
