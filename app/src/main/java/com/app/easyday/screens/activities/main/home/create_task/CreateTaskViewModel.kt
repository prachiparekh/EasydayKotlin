package com.app.easyday.screens.activities.main.home.create_task

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.AddTaskRequestModel
import com.app.easyday.app.sources.remote.model.AttributeResponse
import com.app.easyday.app.sources.remote.model.ProjectParticipantsModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val actionStream: SingleLiveEvent<ACTION> = SingleLiveEvent()
    val projectParticipantsData = MutableLiveData<ArrayList<ProjectParticipantsModel>?>()

    sealed class ACTION {
        class getAttributes(val attributeList: List<AttributeResponse>?, val type: Int) : ACTION()
        class showError(val message: String) : ACTION()
        class taskResponse(val message: String) : ACTION()
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

    fun addAttributes(projectId: Int, type: Int, attributeName: String) {
        DeviceUtils.showProgress()
        api.addAttribute(type, attributeName, projectId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                getAttributes(projectId, type)
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

    fun addTask(addTaskRequestModel: AddTaskRequestModel) {
        DeviceUtils.showProgress()

        val part_project_id: RequestBody = addTaskRequestModel.project_id.toString()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val part_title: RequestBody = addTaskRequestModel.title.toString()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val part_description: RequestBody = addTaskRequestModel.description.toString()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val part_priority: RequestBody = addTaskRequestModel.priority.toString()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val part_red_flag: RequestBody = addTaskRequestModel.red_flag.toString()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val part_due_date: RequestBody = addTaskRequestModel.due_date.toString()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())



        api.addTask(
            part_project_id,
            part_title,
            part_description,
            part_priority,
            part_red_flag,
            part_due_date,
            addTaskRequestModel.tags,
            addTaskRequestModel.zones,
            addTaskRequestModel.spaces,
            addTaskRequestModel.task_media as ArrayList<MultipartBody.Part>, addTaskRequestModel.task_participants
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                if(resp.success)
                    actionStream.value= resp.message?.let { ACTION.taskResponse(it) }

                DeviceUtils.dismissProgress()
            }, {throwable->
                actionStream.value = ErrorUtil.onError(throwable)
                    ?.let { ACTION.showError(it) }
                DeviceUtils.dismissProgress()
            })
    }
}