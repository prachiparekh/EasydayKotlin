package com.app.easyday.screens.activities.auth

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.CreateUserModelToPass
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.activities.main.home.HomeViewModel.Companion.userModel
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun createUser(createuserModel: CreateUserModelToPass) {
        DeviceUtils.showProgress()
        api.createUser(createuserModel)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                actionStream.value = ACTION.onAddUpdateUser(resp.data)
                DeviceUtils.dismissProgress()
            }, { throwable ->
                actionStream.value = ACTION.onError(ErrorUtil.onError(throwable))
                DeviceUtils.dismissProgress()
            })
    }
//    fun createUser(
//        fullName: String,
//        profession: String,
//        profile_image: String?
//    ) {
//
//        val fullNameBody: RequestBody =
//            fullName.toRequestBody("text/plain".toMediaTypeOrNull())
//        val professionBody: RequestBody =
//            profession.toRequestBody("text/plain".toMediaTypeOrNull())
//
//        api.createUser(fullName, profession, profile_image)
//            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ resp ->
//                actionStream.value = ACTION.onAddUpdateUser(resp.data)
//            }, { throwable ->
//                actionStream.value = ACTION.onError(ErrorUtil.onError(throwable))
//            })
//    }

    fun getProfile() {
        api.getProfile()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                userData.value = resp.data
            }, {

            })
    }

    fun updateUser(createuserModel: CreateUserModelToPass) {
        DeviceUtils.showProgress()
        api.updateUser(createuserModel)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                userData.value = resp.data
                userModel = resp.data
                DeviceUtils.dismissProgress()
            }, {
                DeviceUtils.dismissProgress()
            })
    }

}