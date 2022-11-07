package com.app.easyday.screens.activities.main.home.task_detail.discussion

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.CommentResponseItem
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
        comment: RequestBody?,
        parent_id: Int?,
        audioList: ArrayList<File>?
    ) {

        val attachmentBodyList: ArrayList<MultipartBody.Part> = ArrayList()
        audioList?.indices?.forEach { i ->
            val requestList: RequestBody =
                audioList[i].asRequestBody("*/*".toMediaTypeOrNull())

            val attachmentBody =
                MultipartBody.Part.createFormData(
                    "task_comment_media",
                    audioList[i].name,
                    requestList
                )


            attachmentBodyList.add(attachmentBody)
        }

        api.addComment(task_id, comment, parent_id, attachmentBodyList)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                commentList.value = resp.data
            }, {
                commentList.value = null
            })
    }

}

