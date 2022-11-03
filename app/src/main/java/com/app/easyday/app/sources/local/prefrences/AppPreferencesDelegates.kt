package com.app.easyday.app.sources.local.prefrences


class AppPreferencesDelegates private constructor() {
    var wasOnboardingSeen by WasOnboardingSeenDelegate()
    var token by TokenDelegate()
    var showcaseSeen by ShowcaseSeenDelegate()
    var searchList by SearchTaskDelegate()

    companion object {
        private var INSTANCE: AppPreferencesDelegates? = null
        fun get(): AppPreferencesDelegates = INSTANCE ?: AppPreferencesDelegates()
    }

}