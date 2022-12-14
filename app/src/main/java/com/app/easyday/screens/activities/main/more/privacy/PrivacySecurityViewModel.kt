package com.app.easyday.screens.activities.main.more.privacy

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.DeletelogoutResponse
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PrivacySecurityViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val userData = MutableLiveData<DeletelogoutResponse?>()
    fun deleteProfile() {

        api.deleteUser()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                if (resp.success) {
                    userData.value = resp?.data
                }
                DeviceUtils.dismissProgress()
            }, {


                DeviceUtils.dismissProgress()
            })
    }

}