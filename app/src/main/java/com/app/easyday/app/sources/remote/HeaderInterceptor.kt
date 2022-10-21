package com.app.easyday.app.sources.remote

import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val builder = if (AppPreferencesDelegates.get().token.isNotEmpty())
            AppPreferencesDelegates.get().token.let {
                original.newBuilder()
                    .header("Authorization", it)
            }
        else
            original.newBuilder()
        val request = builder.build()
        return chain.proceed(request)

    }
}