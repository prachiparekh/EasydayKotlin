package com.app.easyday.screens.activities.main.more.privacy

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.screens.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PrivacySecurityViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val userData = MutableLiveData<UserModel?>()
    fun deleteProfile() {
        api.getProfile()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                userData.value = resp.data
            }, {

            })
    }

}