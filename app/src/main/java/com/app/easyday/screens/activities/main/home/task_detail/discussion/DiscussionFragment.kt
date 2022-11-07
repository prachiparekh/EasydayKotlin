package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.Manifest
import android.content.ContextWrapper
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DiscussionInterface
import com.app.easyday.app.sources.remote.model.CommentResponseItem
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.IntentUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_discussion.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*


@AndroidEntryPoint
class DiscussionFragment : BaseFragment<DiscussionViewModel>(), DiscussionInterface {

    override fun getContentView() = R.layout.fragment_discussion
    var commentAdapter: CommentsAdapter? = null
    private var commentList: ArrayList<CommentResponseItem>? = null
    private val recorder = MediaRecorder()
    private var taskModel: TaskResponse? = null
    var outputMediaFile: File? = null

    override fun initUi() {

        taskModel = arguments?.getParcelable("taskModel") as TaskResponse?

        add_commentTV.setOnClickListener {
            bottom_RL.isVisible = true
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
                taskModel?.id.let {
                    if (it != null) {
                        viewModel.addComment(it, commentBody, null, null)
                    }
                }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stop_recordTV.setOnClickListener {
            start_recordRL.isVisible = true
            stop_recordRL.isVisible = false
            running = false
            seconds = 0
            recorder.stop()
            runnable?.let { it1 -> handler.removeCallbacks(it1) }
            taskModel?.id?.let { it1 -> viewModel.addComment(it1, null, null, null) }
        }

        recordBtn.setOnClickListener {
            if (IntentUtil.readPermission(
                    requireActivity()
                ) && IntentUtil.writePermission(
                    requireActivity()
                ) && IntentUtil.recordAudioPermission(
                    requireActivity()
                )
            ) {

                startRecording()
            } else {
                onAudioPermission()
            }

        }
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

    private fun onAudioPermission() {

        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted() == true)
                        startRecording()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }

            }).withErrorListener {}

            .check()
    }

    private fun startRecording() {
        start_recordRL.isVisible = false
        stop_recordRL.isVisible = true

        outputMediaFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cw = ContextWrapper(requireContext().applicationContext)
            val directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            File(directory.toString() + "/" + System.currentTimeMillis() + ".m4a")
        } else {
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + "/EasyDay"
            )
            if (!folder.exists()) folder.mkdirs()
            File(folder.absolutePath + "/" + System.currentTimeMillis() + ".m4a")
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setOutputFile(outputMediaFile?.path)
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);         for mp3 audio
        //            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);         for mp3 audio
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.prepare()
        recorder.start()

        running = true
        runTimer()
    }


    var seconds = 0
    var running = false
    var wasRunning = false
    val handler = Handler()
    var runnable: Runnable? = null
    private fun runTimer() {

        runnable = object : Runnable {

            override fun run() {
                val hours: Int = seconds / 3600
                val minutes: Int = seconds % 3600 / 60
                val secs: Int = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes, secs
                    )

                audioTime.text = time

                if (running) {
                    seconds++
                }

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable as Runnable)
    }

    override fun onPause() {
        super.onPause()
        wasRunning = running
        running = false
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.
    override fun onResume() {
        super.onResume()
        if (wasRunning) {
            running = true
        }
    }

}