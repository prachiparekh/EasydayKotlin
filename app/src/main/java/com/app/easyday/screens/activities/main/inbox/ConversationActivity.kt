package com.app.easyday.screens.activities.main.inbox

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
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.easyday.R
import com.app.easyday.utils.IntentUtil
import com.app.easyday.views.ChatAudioRecordView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_conversation.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ConversationActivity : AppCompatActivity(), ChatAudioRecordView.RecordingListener {

    var audioRecordView: ChatAudioRecordView? = null
    private val recorder = MediaRecorder()
    var outputMediaFile: File? = null
    var mediaPlayer: MediaPlayer? = null
    var mTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        this.window?.statusBarColor = resources.getColor(R.color.white)

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        iv_back.setOnClickListener { onBackPressed() }

        edt_msg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null && p0.length >= 1) {
                    cam.visibility = View.GONE
                    mic.visibility = View.GONE
                    attach.visibility = View.VISIBLE
                    send.visibility = View.VISIBLE

                } else {
                    cam.visibility = View.VISIBLE
                    mic.visibility = View.VISIBLE
                    attach.visibility = View.VISIBLE
                    send.visibility = View.GONE
                }


            }

        })

        startIV.setOnClickListener {
            no_message_container.visibility = View.GONE
            conversation_container.visibility = View.VISIBLE
        }

        mic.setOnClickListener {

            if (IntentUtil.readPermission(this) && IntentUtil.writePermission(this) && IntentUtil.recordAudioPermission(this)) {
                layoutAudio.isVisible = true
                conv_rel.isVisible = false
            } else {
                onAudioPermission()
            }

            audioRecordView = ChatAudioRecordView()
            audioRecordView?.initView(layoutAudio)
            audioRecordView?.setRecordingListener(this)
        }

    }
    private fun onAudioPermission() {

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted() == true) {
                        layoutAudio.isVisible = true
                        conv_rel.isVisible = false
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


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onRecordingStarted() {
        startRecording()
    }

    override fun onRecordingCompleted() {
        conv_rel.isVisible = true
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

                val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
                timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
                vidDurationTV.text = duration?.toLong()?.let { Date(it) }
                    ?.let { timeFormatter.format(it) }

                milliSecLeft = duration?.toLong() ?: 0
                if (duration != null) {
                    vidProgressPB.max = duration
                }
                vidProgressPB.progress = 0

                vidPlayerButtonIV.setOnClickListener {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        mTimer?.cancel()
                        vidPlayerButtonIV.setImageResource(R.drawable.ic_video_circle)
                    } else {
                        mediaPlayer?.start()
                        if (milliSecLeft == duration?.toLong()) {
                            timerStart(duration.toLong(), duration)
                        } else {
                            if (duration != null) {
                                timerStart(milliSecLeft, duration)
                            }
                        }
                        vidPlayerButtonIV.setImageResource(R.drawable.ic_pause)
                    }
                }

//                ctaMedia.setOnClickListener {
//                    mediaPlayer?.stop()
//                    mediaPlayer?.reset()
////                    mediaPlayer?.release()
//                    DeviceUtils.showProgress()
//                    outputMediaFile?.let { it1 -> uploadAudioTos3(it1, duration) }
//                }
            }

            mediaPlayer?.setDataSource(this, Uri.parse(outputMediaFile?.absolutePath))
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.prepareAsync()


        } catch (e: Exception) {

            e.printStackTrace()
        }


    }

    override fun onRecordingCanceled() {
        layoutAudio.isVisible = false
        conv_rel.isVisible = true
        recorder.stop()
    }

    private fun startRecording() {
        conv_rel.isVisible = false

        outputMediaFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cw = ContextWrapper(this.applicationContext)
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
    var milliSecLeft: Long = 0
    private fun timerStart(timeLengthMilli: Long, totalDuration: Int) {

        milliSecLeft = timeLengthMilli
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
        mTimer = object : CountDownTimer(timeLengthMilli, 1000) {
            override fun onTick(milliTillFinish: Long) {
                milliSecLeft = milliTillFinish
                vidDurationTV.text = timeFormatter.format(Date(milliSecLeft))
                vidProgressPB.progress = totalDuration - milliSecLeft.toInt()
            }

            override fun onFinish() {
                mTimer?.cancel()
                milliSecLeft = totalDuration.toLong()
                vidProgressPB.progress = 0
                vidDurationTV.text = timeFormatter.format(Date(totalDuration.toLong()))
            }
        }.start()

    }

}