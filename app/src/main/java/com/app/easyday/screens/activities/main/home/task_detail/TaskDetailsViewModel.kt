package com.app.easyday.screens.activities.main.home.task_detail

import com.app.easyday.screens.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor() : BaseViewModel() {

    /* val actionStream: SingleLiveEvent<TaskDetailsViewModel.ACTIONN> = SingleLiveEvent()
     val userActivityData = MutableLiveData<ArrayList<ProjectParticipantsModel>?>()

     sealed class ACTIONN {
         class getAttributes(val attributeList: List<AttributeResponse>?, val type: Int) : ACTIONN()
         class showError(val message: String) : ACTIONN()
         class taskResponse(val message: String) : ACTIONN()
     }

     fun getAttributes(projectId: Int, type: Int) {
         DeviceUtils.showProgress()
         api.getAttributes(projectId, type)
             .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
             .subscribe({ resp ->
                 actionStream.value = TaskDetailsViewModel.ACTIONN.getAttributes(resp.data, type)

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
                 userActivityData.value = resp?.data

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
                     actionStream.value= resp.message?.let { TaskDetailsViewModel.ACTIONN.taskResponse(it) }

                 DeviceUtils.dismissProgress()
             }, {throwable->
                 actionStream.value = ErrorUtil.onError(throwable)
                     ?.let { TaskDetailsViewModel.ACTIONN.showError(it) }
                 DeviceUtils.dismissProgress()
             })
     }*/
}