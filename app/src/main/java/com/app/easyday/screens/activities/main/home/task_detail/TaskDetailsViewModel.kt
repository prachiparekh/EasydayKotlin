package com.app.easyday.screens.activities.main.home.task_detail

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.ProjectRespModel
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.screens.activities.main.home.HomeViewModel
import com.app.easyday.screens.activities.main.home.filter.FilterFragment
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.ErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor() :BaseViewModel()
{}