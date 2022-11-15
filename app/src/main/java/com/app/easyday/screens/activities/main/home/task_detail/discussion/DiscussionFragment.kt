package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.Manifest
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.aws.AWSKeys
import com.app.easyday.app.sources.aws.S3Uploader
import com.app.easyday.app.sources.aws.S3Utils.generates3ShareUrl
import com.app.easyday.app.sources.local.interfaces.DiscussionInterface
import com.app.easyday.app.sources.remote.model.CommentResponseItem
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
import java.text.SimpleDateFormat
import java.util.*


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

    var mediaPlayer: MediaPlayer? = null
    var mTimer: CountDownTimer? = null

    override fun initUi() {
        DeviceUtils.initProgress(requireContext())
        DeviceUtils.showProgress()
        s3uploaderObj = S3Uploader(requireContext(), AWSKeys.FOLDER_NAME_TASK_COMMENT_MEDIA)
        taskId = arguments?.getInt("taskId")
        taskId?.let { viewModel.getComments(it) }

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        add_commentTV.setOnClickListener {
            parentCommentId = null
            bottom_RL.isVisible = true
            commentRL.isVisible = true
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

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (parentCommentId != null) {
                    parentCommentId = null
                    parentRL.isVisible = false
                } else {
                    if (layoutAudio.isVisible || videoProgressCL.isVisible) {
                        layoutAudio.isVisible = false
                        videoProgressCL.isVisible = false
                        bottom_RL.isVisible = false
                    } else {
                        Navigation.findNavController(requireView()).popBackStack()
                    }
                }

                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                    mediaPlayer?.reset()
                }
                if (mTimer != null)
                    mTimer?.cancel()

                true
            } else false
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordBtn.setOnClickListener {

            if (IntentUtil.readPermission(
                    requireActivity()
                ) && IntentUtil.writePermission(
                    requireActivity()
                ) && IntentUtil.recordAudioPermission(
                    requireActivity()
                )
            ) {
                layoutAudio.isVisible = true
                commentRL.isVisible = false
            } else {
                onAudioPermission()
            }

            audioRecordView = AudioRecordView()
            audioRecordView?.initView(layoutAudio)
            audioRecordView?.setRecordingListener(this)
        }
    }

    override fun setObservers() {
        viewModel.commentList.observe(viewLifecycleOwner) { list ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
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
                    discussionCount.text = requireContext().resources.getString(
                        R.string.discussion_str,
                        "0"
                    )
                }

                videoProgressCL.isVisible = false
                layoutAudio.isVisible = false
                bottom_RL.isVisible = false
                discussionCount.isVisible = true
            }
            DeviceUtils.dismissProgress()
        }

        viewModel.likeResponse.observe(viewLifecycleOwner) {

            if (it != null) {
                commentAdapter?.setLikeButton(it)
                DeviceUtils.dismissProgress()
            }
        }
    }

    override fun onLikeClick(commentID: Int) {
        DeviceUtils.showProgress()
        viewModel.likeComment(commentID)
    }

    override fun onReplyClick(parentComment: CommentResponseItem) {
        commentET.text = null
        bottom_RL.isVisible = true
        parentRL.isVisible = true
        this.parentCommentId = parentComment.id
        parentName.text =
            requireContext().resources.getString(R.string.replying_to, parentComment.user?.fullname)
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
                    if (p0?.areAllPermissionsGranted() == true) {
                        layoutAudio.isVisible = true
                        commentRL.isVisible = false
                    }
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

            }
        })
    }

    override fun onRecordingStarted() {
        startRecording()
    }

    override fun onRecordingCompleted() {
        videoProgressCL.isVisible = true
        layoutAudio.isVisible = false
        try {
            recorder.stop()
        } catch (stopException: RuntimeException) {

        }

        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()

            }
            mediaPlayer?.reset()
            mediaPlayer?.setOnPreparedListener { mp ->

                val duration = mediaPlayer?.duration
                Log.e("duration:", duration.toString())
                val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
                timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
                vidDuration.text = duration?.toLong()?.let { Date(it) }
                    ?.let { timeFormatter.format(it) }

                milliSecLeft = duration?.toLong() ?: 0
                if (duration != null) {
                    vidProgress.max = duration
                }
                vidProgress.progress = 0

                vidPlayerButton.setOnClickListener {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        mTimer?.cancel()
                    } else {
                        mediaPlayer?.start()
                        if (milliSecLeft == duration?.toLong()) {
                            timerStart(duration.toLong(), duration)
                        } else {
                            if (duration != null) {
                                timerStart(milliSecLeft, duration)
                            }
                        }
                    }
                }

                ctaMedia.setOnClickListener {
                    mediaPlayer?.stop()
                    mediaPlayer?.reset()
//                    mediaPlayer?.release()
                    DeviceUtils.showProgress()
                    outputMediaFile?.let { it1 -> uploadAudioTos3(it1, duration) }
                }
            }

            mediaPlayer?.setDataSource(requireContext(), Uri.parse(outputMediaFile?.absolutePath))
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.prepareAsync()


        } catch (e: Exception) {
            Log.e("eee::", e.message.toString())
            e.printStackTrace()
        }


    }

    var milliSecLeft: Long = 0
    private fun timerStart(timeLengthMilli: Long, totalDuration: Int) {

        milliSecLeft = timeLengthMilli
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
        mTimer = object : CountDownTimer(timeLengthMilli, 1000) {
            override fun onTick(milliTillFinish: Long) {
                milliSecLeft = milliTillFinish
                vidDuration.text = timeFormatter.format(Date(milliSecLeft))
                vidProgress.progress = totalDuration - milliSecLeft.toInt()
            }

            override fun onFinish() {
                mTimer?.cancel()
                milliSecLeft = totalDuration.toLong()
                vidProgress.progress = 0
                vidDuration.text = timeFormatter.format(Date(totalDuration.toLong()))
            }
        }.start()

    }

    override fun onRecordingCanceled() {
        bottom_RL.isVisible = true
        layoutAudio.isVisible = false
        recorder.stop()
    }


}