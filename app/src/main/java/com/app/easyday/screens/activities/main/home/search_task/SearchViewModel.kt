package com.app.easyday.screens.activities.main.home.search_task

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import com.app.easyday.screens.activities.main.home.HomeViewModel
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val taskList = MutableLiveData<ArrayList<TaskResponse>?>()
    val actionStream: SingleLiveEvent<ACTION> = SingleLiveEvent()

    override fun onFragmentCreated() {
        super.onFragmentCreated()
        selectedProjectID?.let { getAllTask(it) }
    }

    sealed class ACTION {
        class showError(val message: String) : ACTION()
    }

    private fun getAllTask(projectId: Int) {

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

}