package com.app.easyday.app.sources.local.prefrences

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import com.pixplicity.easyprefs.library.Prefs

class WasOnboardingSeenDelegate : ReadWriteProperty<AppPreferencesDelegates, Boolean> {

    companion object {
        const val PREF_KEY_WAS_ONBOARDING_SEEN = "was_onboarding_seen"
    }

    override fun getValue(thisRef: AppPreferencesDelegates, property: KProperty<*>): Boolean =
        Prefs.getBoolean(PREF_KEY_WAS_ONBOARDING_SEEN, false)


    override fun setValue(
        thisRef: AppPreferencesDelegates,
        property: KProperty<*>,
        value: Boolean
    ) {
        Prefs.putBoolean(PREF_KEY_WAS_ONBOARDING_SEEN, value)
    }
}
