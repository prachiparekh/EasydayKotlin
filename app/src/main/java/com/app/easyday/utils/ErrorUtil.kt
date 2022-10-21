package com.app.easyday.utils

import org.json.JSONObject
import retrofit2.adapter.rxjava.HttpException

class ErrorUtil {
    companion object{
        fun onError(throwable: Throwable):String?{
            if (throwable is HttpException) {
                if (throwable.code() == 400 || throwable.code() == 404) {
                    val responseBody = throwable.response()?.errorBody()?.string()
                    val jsonObject = responseBody?.trim()?.let { JSONObject(it) }
                    return jsonObject?.getString("message")
                }
            }
           return null
        }
    }
}