package com.app.easyday.screens.activities.main.home.project

import android.util.Log
import com.app.easyday.app.sources.ApiResponse
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.AddProjectRequestModel
import com.app.easyday.app.sources.remote.model.AddProjectRequestModelToPass
import com.app.easyday.app.sources.remote.model.ProjectRespModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val actionStream: SingleLiveEvent<ProjectViewModel.ACTION> = SingleLiveEvent()

    sealed class ACTION {
        class ProjectResponseSuccess(val response: ApiResponse<ProjectRespModel>) : ACTION()
        class ProjectResponseError(val msg: String) : ACTION()
    }

    fun createProject(createProjectModel: AddProjectRequestModelToPass) {


        api.createProject(
            createProjectModel
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                actionStream.value = resp?.let { it4 ->
                    ACTION.ProjectResponseSuccess(
                        it4
                    )
                }

                DeviceUtils.dismissProgress()
            }, {

                actionStream.value =
                    ACTION.ProjectResponseError(it.message.toString())
                DeviceUtils.dismissProgress()
            })
    }


}