package com.app.easyday.screens.activities.auth

import android.util.Log
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.VerifyOTPRespModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


@HiltViewModel
class OTPViewModel @Inject constructor(
    private val api: EasyDayApi
) : BaseViewModel() {

    val actionStream: SingleLiveEvent<ACTION> = SingleLiveEvent()

    sealed class ACTION {
        class VerifyOTP(val result: Boolean, val msg: String?) : ACTION()
        class IsNewUser(val model: VerifyOTPRespModel) : ACTION()
        class GetOTPMsg(val msg: String) : ACTION()
    }

    fun resendOTP(fullNumber: String,country_code: String) {
        DeviceUtils.showProgress()
        api.sendOTP(fullNumber,country_code)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                actionStream.value = ACTION.GetOTPMsg(resp.message.toString())
                DeviceUtils.dismissProgress()
            }, {

                DeviceUtils.dismissProgress()
            })
    }

    fun verifyOTP(phone_number: String, otp: String,country_code: String,deviceID:String,deviceName:String) {
        DeviceUtils.showProgress()

        /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val deviceToken = task.result

            Log.e("deviceToken",deviceToken)

        })*/

            api.verifyOTP(phone_number, otp, country_code, "deviceToken",deviceID,deviceName)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    DeviceUtils.dismissProgress()
                    if (resp?.success == true)
                        actionStream.value = resp.data?.let { ACTION.IsNewUser(it) }

                    actionStream.value = resp?.message?.let { ACTION.VerifyOTP(resp.success, it) }
                }, {
                    DeviceUtils.dismissProgress()
                    actionStream.value = ACTION.VerifyOTP(false, null)

                })



    }
}