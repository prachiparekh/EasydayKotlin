package com.app.easyday.screens.activities.main.more.notepad

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.DeletelogoutResponse
import com.app.easyday.app.sources.remote.model.FeedbackResponse
import com.app.easyday.app.sources.remote.model.NoteResponse
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.screens.base.BaseViewModel
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(val api: EasyDayApi): BaseViewModel() {

    val notepadData = MutableLiveData<ArrayList<NoteResponse>?>()
    val noteData = MutableLiveData<DeletelogoutResponse?>()

    fun getNote(project_id: Int) {
        api.getNote(project_id)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->

                if (resp.success) {
                    notepadData.value = resp.data
                }


                DeviceUtils.dismissProgress()
            }, {

                DeviceUtils.dismissProgress()
            })
    }


    fun deleteNote() {

        api.deleteNote()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                if (resp.success) {
                    noteData.value = resp?.data
                }
                DeviceUtils.dismissProgress()
            }, {


                DeviceUtils.dismissProgress()
            })
    }
}