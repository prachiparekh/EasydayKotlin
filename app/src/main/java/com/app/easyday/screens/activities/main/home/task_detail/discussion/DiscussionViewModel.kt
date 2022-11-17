package com.app.easyday.screens.activities.main.home.task_detail.discussion

import androidx.lifecycle.MutableLiveData
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import com.app.easyday.app.sources.remote.model.CommentMediaRequest
import com.app.easyday.app.sources.remote.model.CommentResponseItem
import com.app.easyday.app.sources.remote.model.LikeCommentResponse
import com.app.easyday.app.sources.remote.model.TaskCommentMedia
import com.app.easyday.navigation.UiEvent
import com.app.easyday.screens.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


@HiltViewModel
class DiscussionViewModel @Inject constructor(
    val api: EasyDayApi
) : BaseViewModel() {

    val commentList = MutableLiveData<ArrayList<CommentResponseItem>?>()
    val likeResponse = MutableLiveData<LikeCommentResponse?>()

    fun getComments(taskId: Int) {
        api.getTaskComments(taskId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                commentList.value = resp.data
            }, {
                commentList.value = null
            })
    }

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
        taskCommentMedia: TaskCommentMedia?,
        parent_id: Int?
    ) {

        uiEventStream.value = UiEvent.HideKeyboard

        val mediaList = arrayListOf<TaskCommentMedia>()
        if (taskCommentMedia != null) {
            mediaList.add(taskCommentMedia)
        }

        if (taskCommentMedia != null) {
            api.addCommentMedia(CommentMediaRequest(task_id, parent_id, mediaList))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    commentList.value = resp.data
                }, {
                    commentList.value = null
                })
        }
    }

    fun likeComment(commentID: Int) {

        api.likeComment(commentID)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                likeResponse.value = resp.data

            }, {

            })
    }


}

