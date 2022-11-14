package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DiscussionInterface
import com.app.easyday.app.sources.remote.model.CommentResponseItem
import com.app.easyday.app.sources.remote.model.TaskMediaItem
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class CommentsAdapter(
    private val context: Context,
    private var commentList: ArrayList<CommentResponseItem>,
    val anInterface: DiscussionInterface
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    var mediaPlayer: MediaPlayer? = null
    var mTimer: CountDownTimer? = null
    override fun getItemCount(): Int = commentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_comment, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val commenterName = itemView.findViewById<TextView>(R.id.commenterName)
        val timeTV = itemView.findViewById<TextView>(R.id.timeTV)
        val commentTV = itemView.findViewById<TextView>(R.id.commentTV)
        val reply = itemView.findViewById<TextView>(R.id.reply)
        val likeTV = itemView.findViewById<TextView>(R.id.likeTV)
        val videoProgressCL = itemView.findViewById<ConstraintLayout>(R.id.videoProgressCL1)
        val vidPlayerButton1 = itemView.findViewById<ImageView>(R.id.vidPlayerButton1)
        val vidProgress1 = itemView.findViewById<ProgressBar>(R.id.vidProgress1)
        val vidDuration1 = itemView.findViewById<TextView>(R.id.vidDuration1)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            val item = commentList[position]
            commenterName.text = item.user?.fullname

            val odt = OffsetDateTime.parse(item.createdAt)
            val dtf = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

            timeTV.text = dtf.format(odt)
            if (item.comment != null && item.taskCommentMedia?.isEmpty() == true) {
                commentTV.text = item.comment
                commentTV.isVisible = true
                videoProgressCL.isVisible = false
            } else {
                commentTV.isVisible = false
                videoProgressCL.isVisible = true

                if (item.taskCommentMedia?.isNotEmpty() == true) {
                    if (item.taskCommentMedia[0]?.duration != null) {
                        val duration = item.taskCommentMedia[0]?.duration
                        vidDuration1.text = String.format(
                            "%d:%d",
                            duration?.toLong()?.let { TimeUnit.MILLISECONDS.toMinutes(it) },
                            duration?.toLong()?.let { TimeUnit.MILLISECONDS.toSeconds(it) }
                                ?.minus(
                                    TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(
                                            duration.toLong()
                                        )
                                    )
                                ) ?: 0
                        )


                        vidPlayerButton1.setOnClickListener {

                            item.taskCommentMedia[0]?.let { it1 ->
                                initializeMediaPlayer(
                                    it1
                                )
                            }
                        }
                    }
                }
            }

            reply.setOnClickListener {
                item.id?.let { it1 -> anInterface.onReplyClick(it1) }
            }

            likeTV.setOnClickListener {
                item.id?.let { it1 -> anInterface.onLikeClick(it1) }
            }
        }

        private fun initializeMediaPlayer(
            mediaModel: TaskMediaItem
        ) {

            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }

            if (mTimer != null)
                mTimer?.cancel()

            val separated: List<String>? = mediaModel.mediaUrl?.split("?")

            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.stop()

                    }
                }
                mediaPlayer?.setOnPreparedListener { mp ->

                    mp.start()
                    val duration = mediaPlayer?.duration
                    vidProgress1.progress = 0
                    if (duration != null) {
                        vidProgress1.max = duration
                    }
                    duration?.toLong()?.let { timerStart(it, duration) }
                }
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(separated?.get(0).toString())
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
                    vidDuration1.text = timeFormatter.format(Date(milliSecLeft))
                    vidProgress1.progress = totalDuration - milliSecLeft.toInt()
                }

                override fun onFinish() {
                    mTimer?.cancel()
                    milliSecLeft = totalDuration.toLong()
                    vidProgress1.progress = 0
                    vidDuration1.text = timeFormatter.format(Date(totalDuration.toLong()))
                }
            }.start()

        }

    }

    fun setItemList(list: ArrayList<CommentResponseItem>) {
        this.commentList.clear()
        this.commentList.addAll(list)
        notifyDataSetChanged()
    }


}