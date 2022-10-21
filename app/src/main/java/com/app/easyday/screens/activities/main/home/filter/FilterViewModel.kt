package com.app.easyday.screens.activities.main.home.filter

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.AttributeResponse
import com.app.easyday.app.sources.remote.model.ProjectParticipantsModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    val api: EasyDayApi
):BaseViewModel() {

    val actionStream: SingleLiveEvent<ACTION> = SingleLiveEvent()
    val projectParticipantsData = MutableLiveData<ArrayList<ProjectParticipantsModel>?>()

    override fun onFragmentCreated() {
        super.onFragmentCreated()
        selectedProjectID?.let { getAttributes(it,0) }
        selectedProjectID?.let { getAttributes(it,1) }
        selectedProjectID?.let { getAttributes(it,2) }
        selectedProjectID?.let { getProjectParticipants(it) }
    }

    sealed class ACTION {
        class getAttributes(val attributeList: List<AttributeResponse>?, val type: Int) : ACTION()
    }

    fun getAttributes(projectId: Int, type: Int) {
        DeviceUtils.showProgress()
        api.getAttributes(projectId, type)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                actionStream.value = ACTION.getAttributes(resp.data, type)

                DeviceUtils.dismissProgress()
            }, {


                DeviceUtils.dismissProgress()
            })
    }

    fun getProjectParticipants(projectId: Int) {
        DeviceUtils.showProgress()
        api.getProjectParticipants(projectId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                projectParticipantsData.value = resp?.data

                DeviceUtils.dismissProgress()
            }, {


                DeviceUtils.dismissProgress()
            })
    }


}