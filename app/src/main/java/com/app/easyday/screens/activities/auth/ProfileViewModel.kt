package com.app.easyday.screens.activities.auth

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.activities.main.home.HomeViewModel.Companion.userModel
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
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
        profile_image: String?
    ) {

        val fullNameBody: RequestBody =
            fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val professionBody: RequestBody =
            profession.toRequestBody("text/plain".toMediaTypeOrNull())

        api.createUser(fullName, profession, profile_image)
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

    fun updateUser(name: String, profession: String, mImageFile: String?) {
        api.updateUser(name, profession, mImageFile)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                userData.value = resp.data
                userModel = resp.data
            }, {

            })
    }

}