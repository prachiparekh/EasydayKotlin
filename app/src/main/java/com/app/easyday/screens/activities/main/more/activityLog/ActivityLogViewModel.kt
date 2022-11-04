package com.app.easyday.screens.activities.main.more.activityLog

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ActivityLogViewModel @Inject constructor(
    val api: EasyDayApi
    ) : BaseViewModel() {

    val userActivityData = MutableLiveData<ArrayList<UserActivityResponse>?>()

    fun getUserActivityDetails(projectId: Int) {
        DeviceUtils.showProgress()
        api.getUserActivityDetails(projectId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                userActivityData.value = resp?.data

                DeviceUtils.dismissProgress()
            }, {


                DeviceUtils.dismissProgress()
            })
    }
}
