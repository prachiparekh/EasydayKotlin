package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.Manifest
import android.content.ContextWrapper
import android.media.AudioAttributes
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.aws.AWSKeys
import com.app.easyday.app.sources.aws.S3Uploader
import com.app.easyday.app.sources.aws.S3Utils.generates3ShareUrl
import com.app.easyday.app.sources.local.interfaces.DiscussionInterface
import com.app.easyday.app.sources.remote.model.TaskCommentMedia
import com.app.easyday.app.sources.remote.model.TaskMediaItem
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
    var mTimer: CountDownTimer? = null

    companion object {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun initUi() {
        DeviceUtils.initProgress(requireContext())
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

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             mediaPlayer=MediaPlayer().apply {
                 setAudioAttributes(
                     AudioAttributes.Builder()
                         .setUsage(AudioAttributes.USAGE_MEDIA)
                         .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                         .build()
                 )
             }
         } else {
             mediaPlayer=MediaPlayer()
             mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
         }*/

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
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                    mediaPlayer?.reset()
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                if (mTimer != null)
                    mTimer?.cancel()
                if (layoutAudio.isVisible || videoProgressCL.isVisible) {
                    layoutAudio.isVisible = false
                    videoProgressCL.isVisible = false
                    bottom_RL.isVisible = false
                } else {
                    Navigation.findNavController(requireView()).popBackStack()
                }
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

    override fun onAudioBtnClick(
        mediaModel: TaskMediaItem,
        progressBar: ProgressBar,
        durationTV: TextView
    ) {
//        initializeMediaPlayer(mediaModel, progressBar, durationTV)
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
//                    if (p0?.areAllPermissionsGranted() == true)
                    layoutAudio.isVisible = true
                    commentRL.isVisible = false
                    // this is to make your layout the root of audio record view, root layout supposed to be empty..

//                        startRecording()
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
        startRecording()
    }

    override fun onRecordingCompleted() {
        videoProgressCL.isVisible = true
        layoutAudio.isVisible = false
        try {
            recorder.stop()
        } catch (stopException: RuntimeException) {

        }

//        mediaPlayer?.setDataSource(requireContext(), Uri.parse(outputMediaFile?.absolutePath))
        mediaPlayer?.setDataSource(
            requireContext(), Uri.parse(
                "https://s3.eu-west-1.amazonaws.com/easyday-bucket/task_comment_media/1668160340642.mp3?response-content-type=audio%2Fmpeg&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEJL%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCWV1LXdlc3QtMSJHMEUCIHQEkTNLlL9EdTFLWgVZubuKuP2fl%2FJV43DJDuKuKOuhAiEAxYGEEs79rVxwxk1SmKQzzRsazruh2dREX7V79Yh9wkIqmwYIi%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARAAGgwyMjA3MDEzMjEwMjciDNV35JrZQXNksgbSVirvBQUgTS9bMw0J8FJ52h%2BbZhHtLzldxvhPfIzzqHlCXFPGj6qQ3%2FMFVlDtpcmtEGYRybeDtxkkHOUxSxaOAaM7TQqvGAU8%2BsOZ%2FgFgepeKuU5UjdrRkP0hKxIhf%2FW8rQjGsh822r6aP%2FgpuJGmoobfgwGtvFIbLKv5EEX%2BQSYZa9M53O%2F2OI%2FAr7lq8HcSXRLLb89BsZBaC975bqzk3B0fZ%2B1y9%2Bgc8MjLz0hKPEAX5Akm3rJvNQhT7KvKeo3KrAr8eCS6JzU9Q7Doh9ne8v4ha414eDcII%2Fj0p2cPOAa3zFx%2F6f7O8CGvhENR04ikGQQZxI71Gp5QhDhEKNy%2Bdb8kJVoAZf%2F%2FAgrYSMS7c76tHd5NbdAl6%2BmWpsyDfY%2FubZPk2GiRM8A4DJNg8WKJYYe8EmHnYD4WhGRJbH8BIfmjS0o%2BSuEQ2h5wdu026F1Qnkkd%2Bdi%2FLBO2gS5I8ReCY7eC2Es7ZTfS79uw4hDYjAK3PPw8mWwofzsjGtNQUThE85h1DH%2BchIAe9rBSGk62XrHn12KyCKxw7bYhyLjfvpkBYuBOYNbrWElyVCgOFhcavEHDQs76rn63I1Hn0Nv7zHUspmv%2B9CZXxWpKY7eBd8Y2OCeF9oKK7ms28rlTj7AZhmrM4mk6eW8bYMa5kLYnkQXDb7xyTFB34izmbEaoi7zUz2dFavnH1jKKsbADqmhY8w5XLqjylkqEdOMsT8S8gvJMQTUJA49mUE7sMvh5tivhnEOvJ4SNpajwJsGhaEjh5p7rKRr3kkFIVxk%2FJ%2FWrrLRFX6PFIer1Ojn0gLbl93rK%2BzGOVQosgpgkzeMsCBWRBhcY9jO7VVNCs8P9c%2Fr6%2FURpQHhOOEVQefpy8sEf6Mayj28%2BWQ4XugbI0Gv6QPSSnkUg78MVPLxgv4oxp9wozD30C%2FuPVaQRGQo2srLzqiZLhMNQlpHb04urrgYaw8DprFr1GS556G1YfQpZ13%2FDFsYjTasgfKpY8Z50U9TjJLeQD%2FUw37a4mwY6hwIgGhA5xXS8KSFa7Nsz9DY7Z9Va5BhqW3vbi4HyT2uccFjfgQsYApTOmPFteMPelZlYdM5Aw6opVVuw%2FsEDvFX%2FogPZQxJHOzcA2TlXKVJkFqnab7wHvQvM4yG%2BIZle%2BOUTbpzyALNXsNaFFfHw4Xj03tGCptX%2BsdRdyLZCUa7CxjSah9FFTXfvKIiocY5AD4I77UOKTgkgqr%2BzC13a6T9elawjiEsG93Mkhz%2FZ2ORN8crS1%2BGrp5N4X9qHJkK0vjONqirPJmoqKHJvVToJ2BfbXnhfzzSpQcWtNjWAKu3ZCMu8UQlPHLGKp4KtuzPTQfcTyGi7lsFRyWtEu1Y9wxnNxhrAWLD4Eg%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20221111T095231Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3599&X-Amz-Credential=ASIATGYWS45BST5KKEGK%2F20221111%2Feu-west-1%2Fs3%2Faws4_request&X-Amz-Signature=2c9b22016a2fa246b6131e1102460b5d0fafa4db0090781fa4a3b0b0b44cd785"
            )
        )
        mediaPlayer?.prepare()

        val duration = mediaPlayer?.duration
        Log.e("duration:", duration.toString())
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
        vidDuration.text = timeFormatter.format(duration?.toLong()?.let { Date(it) })

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
            mediaPlayer?.release()
            DeviceUtils.showProgress()
            outputMediaFile?.let { it1 -> uploadAudioTos3(it1, duration) }
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