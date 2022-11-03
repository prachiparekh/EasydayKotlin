package com.app.easyday.app.sources.local.prefrences

import com.pixplicity.easyprefs.library.Prefs
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SearchTaskDelegate : ReadWriteProperty<AppPreferencesDelegates, Set<String>> {

    companion object {
        const val PREF_KEY_SEARCH_TASK = "search_task"
    }

    override fun getValue(thisRef: AppPreferencesDelegates, property: KProperty<*>): Set<String> =
        Prefs.getStringSet(PREF_KEY_SEARCH_TASK, emptySet<String>())


    override fun setValue(
        thisRef: AppPreferencesDelegates,
        property: KProperty<*>,
        value: Set<String>
    ) {
        val set: MutableSet<String> = HashSet()
        set.addAll(value)
        Prefs.putStringSet(PREF_KEY_SEARCH_TASK, set)
    }
}
