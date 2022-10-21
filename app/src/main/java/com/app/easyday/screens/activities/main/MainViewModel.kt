package com.app.easyday.screens.activities.main

import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.screens.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val api : EasyDayApi
):BaseViewModel(){
}