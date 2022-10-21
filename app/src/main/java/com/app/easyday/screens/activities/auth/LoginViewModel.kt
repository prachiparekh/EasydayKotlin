package com.app.easyday.screens.activities.auth

import android.util.Log
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import retrofit2.adapter.rxjava.HttpException

import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: EasyDayApi
) : BaseViewModel() {

    val actionStream: SingleLiveEvent<ACTION> = SingleLiveEvent()

    sealed class ACTION {
        class GetOTPMsg(val msg: String) : ACTION()
        class GetErrorMsg(val msg: String) : ACTION()
    }

    fun sendOTP(fullNumber: String,country_code: String) {
        DeviceUtils.showProgress()

        api.sendOTP(fullNumber,country_code)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                actionStream.value = ACTION.GetOTPMsg(resp.message.toString())
                DeviceUtils.dismissProgress()
            }, { throwable ->
                actionStream.value = ErrorUtil.onError(throwable)
                    ?.let { ACTION.GetErrorMsg(it) }
                DeviceUtils.dismissProgress()
            })
    }
}