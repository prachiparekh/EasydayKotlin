package com.app.easyday.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import com.app.easyday.R
import java.text.SimpleDateFormat
import java.util.*

class ChatAudioRecordView {
    enum class UserBehaviour {
        CANCELING, LOCKING, NONE
    }

    enum class RecordingBehaviour {
        CANCELED, LOCKED, RELEASED
    }

    interface RecordingListener {
        fun onRecordingStarted()
        fun onRecordingCompleted()
        fun onRecordingCanceled()
    }

    private val TAG = "AudioRecordView"


    private var imageViewAudio: View? =
        null
    private var imageViewLockArrow: android.view.View? = null
    private var imageViewLock: android.view.View? = null

    private var imageViewSend: android.view.View? = null


    private var layoutSlideCancel: View? =
        null


    private var layoutEffect2: android.view.View? = null
    private var timeText: TextView? =
        null
    private var textViewSlide: android.widget.TextView? = null

    private var animBlink: Animation? =
        null
    private var animJump: android.view.animation.Animation? = null
    private var animJumpFast: android.view.animation.Animation? = null

    private var isDeleting = false
    private var stopTrackingAction = false
    private var handler: Handler? = null

    private var audioTotalTime = 0
    private var timerTask: TimerTask? = null
    private var audioTimer: Timer? = null
    private var timeFormatter: SimpleDateFormat? = null

    private var lastX = 0f
    private var lastY: kotlin.Float = 0f
    private var firstX = 0f
    private var firstY: kotlin.Float = 0f

    private val directionOffset =
        0f
    private var cancelOffset: kotlin.Float = 0f
    private var lockOffset: kotlin.Float = 0f
    private var dp = 0f
    private var isLocked = false

    private var userBehaviour = UserBehaviour.NONE
    private var recordingListener: RecordingListener? = null

    var isLayoutDirectionRightToLeft = false

    var screenWidth = 0
    var screenHeight: kotlin.Int = 0
    private var context: Context? = null


    fun initView(view: ViewGroup?) {
        if (view == null) {
            showErrorLog("initView ViewGroup can't be NULL")
            return
        }
        context = view.context
        view.removeAllViews()
        view.addView(LayoutInflater.from(view.context).inflate(R.layout.chat_record_view, null))
        timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val displayMetrics = view.context.resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        isLayoutDirectionRightToLeft = view.context.resources.getBoolean(R.bool.is_right_to_left)
        textViewSlide = view.findViewById<TextView>(R.id.textViewSlide)
        timeText = view.findViewById(R.id.textViewTime)
        layoutSlideCancel = view.findViewById(R.id.layoutSlideCancel)
        imageViewAudio = view.findViewById(R.id.imageViewAudio)
        handler = Handler(Looper.getMainLooper())
        dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            view.context.resources.displayMetrics
        )
        animBlink = AnimationUtils.loadAnimation(
            view.context,
            R.anim.blink
        )
        animJump = AnimationUtils.loadAnimation(
            view.context,
            R.anim.jump
        )
        animJumpFast = AnimationUtils.loadAnimation(
            view.context,
            R.anim.jump_fast
        )
        setupRecording()

    }


    fun setRecordingListener(recordingListener: RecordingListener?) {
        this.recordingListener = recordingListener
    }

    private fun setupRecording() {
        imageViewSend?.animate()?.scaleX(0f)?.scaleY(0f)?.setDuration(100)?.setInterpolator(
            LinearInterpolator()
        )?.start()

        imageViewAudio?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->

            if (isDeleting) {
                return@OnTouchListener true
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                cancelOffset = (screenWidth / 2.8).toFloat()
                lockOffset = (screenWidth / 2.5).toFloat()
                if (firstX == 0f) {
                    firstX = motionEvent.rawX
                }
                if (firstY == 0f) {
                    firstY = motionEvent.rawY
                }
                startRecord()
            } else if (motionEvent.action == MotionEvent.ACTION_UP
                || motionEvent.action == MotionEvent.ACTION_CANCEL
            ) {
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    stopRecording(RecordingBehaviour.RELEASED)
                }
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                if (stopTrackingAction) {
                    return@OnTouchListener true
                }
                var direction = UserBehaviour.NONE
                val motionX = Math.abs(firstX - motionEvent.rawX)
                val motionY: Float = Math.abs(firstY - motionEvent.rawY)
                if (if (isLayoutDirectionRightToLeft) motionX > directionOffset && lastX > firstX && lastY > firstY else motionX > directionOffset && lastX < firstX && lastY < firstY) {
                    if (if (isLayoutDirectionRightToLeft) motionX > motionY && lastX > firstX else motionX > motionY && lastX < firstX) {
                        direction = UserBehaviour.CANCELING
                    } else if (motionY > motionX && lastY < firstY) {
                        direction = UserBehaviour.LOCKING
                    }
                } else if (if (isLayoutDirectionRightToLeft) motionX > motionY && motionX > directionOffset && lastX > firstX else motionX > motionY && motionX > directionOffset && lastX < firstX) {
                    direction = UserBehaviour.CANCELING
                } else if (motionY > motionX && motionY > directionOffset && lastY < firstY) {
                    direction = UserBehaviour.LOCKING
                }
                if (direction == UserBehaviour.CANCELING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.rawY + (imageViewAudio?.width?.div(
                            2
                        ) ?: 0) > firstY
                    ) {
                        userBehaviour = UserBehaviour.CANCELING
                    }
                    if (userBehaviour == UserBehaviour.CANCELING) {
                        translateX(-(firstX - motionEvent.rawX))
                    }
                } else if (direction == UserBehaviour.LOCKING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.rawX + (imageViewAudio?.width?.div(
                            2
                        ) ?: 0) > firstX
                    ) {
                        userBehaviour = UserBehaviour.LOCKING
                    }
                    if (userBehaviour == UserBehaviour.LOCKING) {
                        translateY(-(firstY - motionEvent.rawY))
                    }
                }
                lastX = motionEvent.rawX
                lastY = motionEvent.rawY
            }
            view.onTouchEvent(motionEvent)
            true
        })

    }

    private fun translateY(y: Float) {
        if (y < -lockOffset) {
            locked()
            imageViewAudio?.translationY = 0f
            return
        }

        imageViewAudio?.translationY = y

        imageViewAudio?.translationX = 0f
    }

    private fun translateX(x: Float) {
        if (if (isLayoutDirectionRightToLeft) x > cancelOffset else x < -cancelOffset) {
            canceled()
            imageViewAudio?.translationX = 0f
            layoutSlideCancel?.translationX = 0f
            return
        }
        imageViewAudio?.translationX = x
        layoutSlideCancel?.translationX = x

        imageViewAudio?.translationY = 0f

    }

    private fun locked() {
        stopTrackingAction = true
        stopRecording(RecordingBehaviour.LOCKED)
        isLocked = true
    }

    private fun canceled() {
        stopTrackingAction = true
        stopRecording(RecordingBehaviour.CANCELED)
    }

    private fun stopRecording(recordingBehaviour: RecordingBehaviour) {
        stopTrackingAction = true
        firstX = 0f
        firstY = 0f
        lastX = 0f
        lastY = 0f
        userBehaviour = UserBehaviour.NONE
        imageViewAudio?.animate()?.scaleX(1f)?.scaleY(1f)?.translationX(0f)?.translationY(0f)
            ?.setDuration(100)?.setInterpolator(
                LinearInterpolator()
            )?.start()
        layoutSlideCancel?.translationX = 0f
        layoutSlideCancel?.visibility = View.GONE
        imageViewLockArrow?.clearAnimation()
        imageViewLock?.clearAnimation()
        if (isLocked) {
            return
        }


        if (recordingBehaviour == RecordingBehaviour.CANCELED) {
            timeText?.visibility = View.INVISIBLE
            layoutSlideCancel?.visibility = View.INVISIBLE
            layoutEffect2?.visibility = View.GONE
            timerTask?.cancel()
            delete()
            if (recordingListener != null) recordingListener?.onRecordingCanceled()
        } else if (recordingBehaviour == RecordingBehaviour.RELEASED) {
            timeText?.visibility = View.INVISIBLE

            layoutEffect2?.visibility = View.GONE

            timerTask?.cancel()
            if (recordingListener != null) recordingListener?.onRecordingCompleted()
        }
    }

    private fun startRecord() {
        if (recordingListener != null) recordingListener?.onRecordingStarted()

        stopTrackingAction = false

        imageViewAudio?.animate()?.scaleXBy(1f)?.scaleYBy(1f)?.setDuration(200)?.setInterpolator(
            OvershootInterpolator()
        )?.start()
        timeText?.visibility = View.VISIBLE
        layoutSlideCancel?.visibility = View.VISIBLE

        layoutEffect2?.visibility = View.VISIBLE

        imageViewLockArrow?.clearAnimation()
        imageViewLock?.clearAnimation()
        imageViewLockArrow?.startAnimation(animJumpFast)
        imageViewLock?.startAnimation(animJump)
        if (audioTimer == null) {
            audioTimer = Timer()
            timeFormatter?.timeZone = TimeZone.getTimeZone("UTC")
        }
        timerTask = object : TimerTask() {
            override fun run() {
                handler?.post {
                    timeText?.text = timeFormatter?.format(Date((audioTotalTime * 1000).toLong()))
                    audioTotalTime++
                }
            }
        }
        audioTotalTime = 0
        audioTimer?.schedule(timerTask, 0, 1000)
    }

    private fun delete() {

        isDeleting = true
        imageViewAudio?.isEnabled = false
        handler?.postDelayed({
            isDeleting = false
            imageViewAudio?.isEnabled = true

        }, 1250)

    }

    private fun showErrorLog(s: String) {

    }

}