package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DiscussionInterface
import com.app.easyday.app.sources.remote.model.CommentResponseItem
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.screens.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_discussion.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


@AndroidEntryPoint
class DiscussionFragment : BaseFragment<DiscussionViewModel>(), DiscussionInterface {

    override fun getContentView() = R.layout.fragment_discussion
    var commentAdapter: CommentsAdapter? = null
    var commentList: ArrayList<CommentResponseItem>? = null

    override fun initUi() {

        val taskModel = arguments?.getParcelable("taskModel") as TaskResponse?

        add_commentTV.setOnClickListener {
            bottom_RL.isVisible = true
        }

        recordBtn.setOnClickListener {
            start_recordRL.isVisible = false
            stop_recordRL.isVisible = true
        }
        stop_recordTV.setOnClickListener {
            start_recordRL.isVisible = true
            stop_recordRL.isVisible = false
        }

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        commentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(newText: Editable?) {
                if (newText.isNullOrEmpty()) {
                    recordBtn.isVisible = true
                    cta.isVisible = false
                } else {
                    recordBtn.isVisible = false
                    cta.isVisible = true
                }
            }
        })

        cta.setOnClickListener {
            if (taskModel?.id != null) {
                val comment = commentET.text
                val commentBody: RequestBody = comment.toString()
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                viewModel.addComment(taskModel.id, commentBody, null, null)
            }
        }


        commentList = taskModel?.taskComments as ArrayList<CommentResponseItem>?
        discussionCount.text = requireContext().resources.getString(
            R.string.discussion_str,
            commentList?.size.toString()
        )
        commentAdapter = commentList?.let {
            CommentsAdapter(
                requireContext(),
                it, this
            )
        }
        commentRV.adapter = commentAdapter

    }

    override fun setObservers() {
        viewModel.commentList.observe(viewLifecycleOwner) {
            commentList?.clear()
            if (it != null) {
                commentList?.addAll(it)
                discussionCount.text = requireContext().resources.getString(
                    R.string.discussion_str,
                    commentList?.size.toString()
                )
                commentAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onLikeClick() {

    }

    override fun onReplyClick() {
    }

}