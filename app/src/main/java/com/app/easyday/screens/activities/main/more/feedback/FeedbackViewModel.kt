package com.app.easyday.screens.activities.main.more.feedback

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.FeedbackResponse
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(val api: EasyDayApi) : BaseViewModel() {

    val userFeedbackData = MutableLiveData<FeedbackResponse>()

    @SuppressLint("NullSafeMutableLiveData")
    fun submitFeedback(rating: String, tags: String, feedback_text: String) {
        api.submitFeedback(rating, tags, feedback_text)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->

                if (resp.success) {
                    userFeedbackData.value = resp.data
                }


                DeviceUtils.dismissProgress()
            }, {

                DeviceUtils.dismissProgress()
            })
    }
}