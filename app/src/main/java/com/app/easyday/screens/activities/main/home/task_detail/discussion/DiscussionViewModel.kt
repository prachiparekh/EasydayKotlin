package com.app.easyday.screens.activities.main.home.task_detail.discussion

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.CommentResponseItem
import com.app.easyday.navigation.UiEvent
import com.app.easyday.screens.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val commentList = MutableLiveData<ArrayList<CommentResponseItem>?>()

    fun addComment(
        task_id: Int,
        comment: RequestBody?, parent_id: Int?
    ) {
        uiEventStream.value = UiEvent.HideKeyboard
        api.addComment(task_id, comment, parent_id)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                commentList.value = resp.data
            }, {
                commentList.value = null
            })
    }


    fun addMediaComment(
        task_id: Int,
        audioFile: File?,
        parent_id: Int?
    ) {

        uiEventStream.value = UiEvent.HideKeyboard
        val requestBody: RequestBody? =
            audioFile?.asRequestBody("*/*".toMediaTypeOrNull())

        val attachmentBody =
            requestBody?.let {
                MultipartBody.Part.createFormData(
                    "task_comment_media",
                    audioFile.name,
                    it
                )
            }

        api.addCommentMedia(task_id, attachmentBody, parent_id)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                commentList.value = resp.data
            }, {
                commentList.value = null
            })
    }


}

