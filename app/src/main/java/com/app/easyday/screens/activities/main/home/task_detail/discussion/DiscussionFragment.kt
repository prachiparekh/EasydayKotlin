package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.Manifest
import android.content.ContextWrapper
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.aws.AWSKeys
import com.app.easyday.app.sources.aws.S3Uploader
import com.app.easyday.app.sources.aws.S3Utils.generates3ShareUrl
import com.app.easyday.app.sources.local.interfaces.DiscussionInterface
import com.app.easyday.app.sources.remote.model.TaskCommentMedia
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.IntentUtil
import com.app.easyday.views.AudioRecordView
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
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class DiscussionFragment : BaseFragment<DiscussionViewModel>(), DiscussionInterface,
    AudioRecordView.RecordingListener {

    override fun getContentView() = R.layout.fragment_discussion
    var commentAdapter: CommentsAdapter? = null
    private val recorder = MediaRecorder()
    private var taskId: Int? = null
    var outputMediaFile: File? = null
    var parentCommentId: Int? = null

    private var s3uploaderObj: S3Uploader? = null
    private var urlFromS3: String? = null

    var audioRecordView: AudioRecordView? = null

    private var timerTask: TimerTask? = null
    private var audioTimer: Timer? = null
    private var handler: Handler? = null


    override fun initUi() {
        DeviceUtils.initProgress(requireContext())
        s3uploaderObj = S3Uploader(requireContext(), AWSKeys.FOLDER_NAME_TASK_COMMENT_MEDIA)
        taskId = arguments?.getInt("taskId")
        taskId?.let { viewModel.getComments(it) }

        add_commentTV.setOnClickListener {
            parentCommentId = null
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
            if (taskId != null) {
                DeviceUtils.showProgress()
                val comment = commentET.text
                val commentBody: RequestBody = comment.toString()
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                taskId.let { id ->
                    if (id != null) {
                        viewModel.addComment(id, commentBody, parentCommentId)
                    }
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordBtn.setOnClickListener {
            layoutMain.isVisible = true
            commentRL.isVisible = false
            // this is to make your layout the root of audio record view, root layout supposed to be empty..
            audioRecordView = AudioRecordView()
            audioRecordView?.initView(layoutMain)
            audioRecordView?.setRecordingListener(this)
        }
    }

    override fun setObservers() {
        viewModel.commentList.observe(viewLifecycleOwner) { list ->

            commentET.text = null
            if (list != null) {
                list.sortByDescending { it.createdAt }
                if (commentAdapter == null) {
                    commentAdapter =
                        CommentsAdapter(
                            requireContext(),
                            list, this
                        )

                    commentRV.adapter = commentAdapter
                } else {
                    commentAdapter?.setItemList(list)
                    commentAdapter?.notifyDataSetChanged()
                }

                discussionCount.text = requireContext().resources.getString(
                    R.string.discussion_str,
                    list.size.toString()
                )
            } else {
                discussionCount.isVisible = false
            }

            DeviceUtils.dismissProgress()
        }
    }

    override fun onLikeClick(commentID: Int) {

    }

    override fun onReplyClick(parentCommentID: Int) {
        commentET.text = null
        bottom_RL.isVisible = true
        this.parentCommentId = parentCommentID
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
        commentRL.isVisible = false

        outputMediaFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cw = ContextWrapper(requireContext().applicationContext)
            val directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            File(directory.toString() + "/" + System.currentTimeMillis() + ".mp3")
        } else {
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + "/EasyDay"
            )
            if (!folder.exists()) folder.mkdirs()
            File(folder.absolutePath + "/" + System.currentTimeMillis() + ".mp3")
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setOutputFile(outputMediaFile?.path)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)       //  for mp3 audio
        recorder.prepare()
        recorder.start()

    }


    private fun uploadAudioTos3(audioFile: File, duration: Int?) {
        val path = audioFile.absolutePath

        s3uploaderObj?.initUpload(path)
        s3uploaderObj?.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
            override fun onUploadSuccess(response: String?) {
                if (response.equals("Success", ignoreCase = true)) {

                    urlFromS3 = generates3ShareUrl(
                        requireContext(),
                        path,
                        AWSKeys.FOLDER_NAME_TASK_COMMENT_MEDIA
                    )
                    if (!TextUtils.isEmpty(urlFromS3)) {


                        taskId?.let { it1 ->
                            viewModel.addMediaComment(
                                it1,
                                TaskCommentMedia(
                                    media_url = urlFromS3,
                                    type = 2,
                                    duration = duration
                                ),
                                parentCommentId
                            )
                        }
                    }
                }
            }

            override fun onUploadError(response: String?) {

                Log.e("TAG", "Error Uploading: $response")
            }
        })
    }

    override fun onRecordingStarted() {
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

    override fun onRecordingCompleted() {
        videoProgressCL.isVisible = true
        layoutMain.isVisible = false
        recorder.stop()

        val mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer.setDataSource(outputMediaFile?.absolutePath)
            mediaPlayer.prepare()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val duration = mediaPlayer.duration
        vidDuration.text = String.format(
            "%d:%d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        )

        vidPlayerButton.setOnClickListener {
            if (mediaPlayer.isPlaying)
                mediaPlayer.pause()
            else {
                mediaPlayer.start()
            }
        }

        ctaMedia.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
            DeviceUtils.showProgress()
            outputMediaFile?.let { it1 -> uploadAudioTos3(it1, duration) }
        }

        if (audioTimer == null) {
            audioTimer = Timer()
        }

        vidProgress.max = duration


    }

    override fun onRecordingCanceled() {
        commentRL.isVisible = true
        layoutMain.isVisible = false
        recorder.stop()
    }


}