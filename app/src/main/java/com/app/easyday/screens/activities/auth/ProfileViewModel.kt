package com.app.easyday.screens.activities.auth

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val userData = MutableLiveData<UserModel?>()
    val actionStream: SingleLiveEvent<ACTION> = SingleLiveEvent()

    sealed class ACTION {
        class onAddUpdateUser(val userData: UserModel?) : ACTION()
        class onError(val msg: String?) : ACTION()
    }

    fun createUser(
        fullName: String,
        profession: String,
        phoneNumber: String,
        country_code: String,
        profile_image: File?, deviceID: String, deviceName: String
    ) {

        val fullNameBody: RequestBody =
            fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val professionBody: RequestBody =
            profession.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneNumberBody: RequestBody =
            phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val countryCodeBody: RequestBody =
            country_code.toRequestBody("text/plain".toMediaTypeOrNull())
        val mPartBody: RequestBody? =
            profile_image?.asRequestBody("image/*".toMediaTypeOrNull())
        val requestFile: MultipartBody.Part? =
            mPartBody?.let {
                MultipartBody.Part.createFormData(
                    "profile_image", profile_image.name,
                    it
                )
            }
        api.createUser(fullNameBody, professionBody, phoneNumberBody, countryCodeBody, requestFile,"deviceToken",deviceID,deviceName)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                actionStream.value = ACTION.onAddUpdateUser(resp.data)
            }, { throwable ->
                actionStream.value = ACTION.onError(ErrorUtil.onError(throwable))
            })
    }

    fun getProfile() {
        api.getProfile()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                userData.value = resp.data
            }, {

            })
    }

}