package com.app.easyday.screens.activities.main.home

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.ProjectRespModel
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterAssigneeList
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterDateRangeList
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterDueDate
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterPriority
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterRedFlag
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterSpaceList
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterTagList
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterZoneList
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val actionStream: SingleLiveEvent<ACTION> = SingleLiveEvent()
    val projectList = MutableLiveData<ArrayList<ProjectRespModel>?>()
    val taskList = MutableLiveData<ArrayList<TaskResponse>?>()

    companion object {
        var userModel: UserModel? = null
    }

    sealed class ACTION {
        class showError(val message: String) : ACTION()
    }


    var userProfileData = MutableLiveData<UserModel?>()

    override fun onFragmentCreated() {
        super.onFragmentCreated()
        getProfile()
        getProjects()
    }

    private fun getProfile() {
        api.getProfile()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                userModel = resp.data
                userProfileData.value = resp.data
            }, {
                userProfileData.value = null
            })
    }

    private fun getProjects() {
        api.getAllProject()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->

                projectList.value = resp.data

            }, {

                projectList.value = null
            })
    }

    fun getAllTask(projectId: Int) {

        api.getTask(
            projectId

        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                taskList.value = resp.data

            }, { throwable ->
                actionStream.value = ErrorUtil.onError(throwable)
                    ?.let { ACTION.showError(it) }
                taskList.value = null
            })
    }

    fun getFilterTask(projectId: Int) {

        api.getTask(
            projectId,
            filterZoneList,
            filterTagList,
            filterSpaceList,
            filterAssigneeList,
            filterRedFlag,
            filterDueDate,
            filterPriority,
            filterDateRangeList
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                taskList.value = resp.data

            }, { throwable ->
                actionStream.value = ErrorUtil.onError(throwable)
                    ?.let { ACTION.showError(it) }
                taskList.value = null
            })
    }
}