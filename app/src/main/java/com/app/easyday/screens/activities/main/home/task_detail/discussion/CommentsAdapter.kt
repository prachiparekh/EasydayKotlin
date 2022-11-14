package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
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
import com.app.easyday.screens.activities.main.home.task_detail.discussion.DiscussionFragment.Companion.mediaPlayer
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

    var duration = 0
    var hdlr = Handler()

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
        val vidPlayerButton = itemView.findViewById<ImageView>(R.id.vidPlayerButton1)
        val vidProgress = itemView.findViewById<ProgressBar>(R.id.vidProgress1)
        val vidDuration = itemView.findViewById<TextView>(R.id.vidDuration1)

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
                        vidDuration.text = String.format(
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


                        vidPlayerButton.setOnClickListener {
                            item.taskCommentMedia[0]?.let { it1 ->
                                initializeMediaPlayer(
                                    it1,
                                    vidProgress,
                                    vidDuration
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
            mediaModel: TaskMediaItem, progressBar: ProgressBar,
            durationTV: TextView
        ) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer?.release()
            }


            Log.e("mediaPlayer::", mediaPlayer.toString())
            try {
                mediaPlayer?.setOnPreparedListener { mp ->
                    Log.e("mp::", "Start")
                    mp.start()
                    ChangeMusic(progressBar, durationTV)
                }
                mediaPlayer?.setDataSource(mediaModel.mediaUrl)
                mediaPlayer?.prepareAsync()

            } catch (e: Exception) {
                Log.e("eee::", e.message.toString())
                e.printStackTrace()
            }
        }

        private fun ChangeMusic(
            progressBar: ProgressBar,
            durationTV: TextView

        ) {
            /*if (mp != null) {
                if (mp?.isPlaying == true) {
                    mp?.stop()
                    mp?.reset()
                    mp?.release()
                    mp =
                        null
                    mp =
                        MediaPlayer()
                }
            } else {
                mp =
                    MediaPlayer()
            }*/

            duration =
                mediaPlayer?.duration?.div(1000) ?: 0
            Log.e("duration::", duration.toString())
            if (mediaPlayer?.isPlaying == true) {
//            mPlay.setVisibility(View.GONE)
//            mPause.setVisibility(View.VISIBLE)
            }
            progressBar.max = duration
            //            //   Log.e("Player duration ",seekbar.getMax() + " ");
            val hours: Int = duration / 3600
            val minutes: Int = duration / 60 - hours * 60
            val seconds: Int = duration - hours * 3600 - minutes * 60
            val formatted: String = if (hours == 0) {
                String.format("%02d:%02d", minutes, seconds)
            } else {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            }
            durationTV.text = formatted + ""
            progressBarAdp = progressBar
            durationTVAdp = durationTV
            hdlr.postDelayed(UpdateSongTime, 100)
        }


        //    From File explorer & transfer
        var progressBarAdp: ProgressBar? = null
        var durationTVAdp: TextView? = null
        private val UpdateSongTime: Runnable = object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    val current: Int =
                        mediaPlayer?.currentPosition?.div(1000) ?: 0
                    progressBarAdp?.progress = current
                    val hours = current / 3600
                    val minutes = current / 60 - hours * 60
                    val seconds = current - hours * 3600 - minutes * 60
                    val formatted: String = if (hours == 0) {
                        String.format("%02d:%02d", minutes, seconds)
                    } else {
                        String.format("%d:%02d:%02d", hours, minutes, seconds)
                    }
                    durationTVAdp?.text = formatted
                    /*  if (startTime.getText() == endTime.getText()) {
                          mPlay.setVisibility(View.VISIBLE)
                          mPause.setVisibility(View.GONE)
                      }*/
                    hdlr.postDelayed(this, 100)
                }
            }
        }


    }

    fun setItemList(list: ArrayList<CommentResponseItem>) {
        this.commentList.clear()
        this.commentList.addAll(list)
        notifyDataSetChanged()
    }


}