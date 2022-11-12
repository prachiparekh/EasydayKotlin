package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DiscussionInterface
import com.app.easyday.app.sources.remote.model.CommentResponseItem
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CommentsAdapter(
    private val context: Context,
    private var commentList: ArrayList<CommentResponseItem>,
    val anInterface: DiscussionInterface
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var mTimer: CountDownTimer? = null
    var milliSecLeftAdp: Long = 0

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
        /* val videoProgressCL = itemView.findViewById<ConstraintLayout>(R.id.videoProgressCL)
         val vidPlayerButton = itemView.findViewById<ImageView>(R.id.vidPlayerButton)
         val vidProgress = itemView.findViewById<ProgressBar>(R.id.vidProgress)
         val vidDuration = itemView.findViewById<TextView>(R.id.vidDuration)*/

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            val item = commentList[position]
            commenterName.text = item.user?.fullname

            val odt = OffsetDateTime.parse(item.createdAt)
            val dtf = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

            timeTV.text = dtf.format(odt)
            commentTV.text = item.comment
//            if (item.comment != null && item.taskCommentMedia?.isEmpty() == true) {
//                commentTV.text = item.comment
//                commentTV.isVisible = true
//                videoProgressCL.isVisible = false
//            } else {
//                commentTV.isVisible = false
//                videoProgressCL.isVisible = true
//
//                /*if (item.taskCommentMedia?.isNotEmpty() == true) {
//                    if (item.taskCommentMedia[0]?.duration != null) {
//                        val duration = item.taskCommentMedia[0]?.duration
//                        vidDuration.text = String.format(
//                            "%d:%d",
//                            duration?.toLong()?.let { TimeUnit.MILLISECONDS.toMinutes(it) },
//                            duration?.toLong()?.let { TimeUnit.MILLISECONDS.toSeconds(it) }
//                                ?.minus(
//                                    TimeUnit.MINUTES.toSeconds(
//                                        TimeUnit.MILLISECONDS.toMinutes(
//                                            duration.toLong()
//                                        )
//                                    )
//                                ) ?: 0
//                        )
//
//
//                        vidPlayerButton.setOnClickListener {
////                            item.taskCommentMedia[0]?.let { it1 -> onAudioBtnClick(it1,vidProgress,vidDuration) }
//                        }
//                    }
//                }*/
//            }

            reply.setOnClickListener {
                item.id?.let { it1 -> anInterface.onReplyClick(it1) }
            }

            likeTV.setOnClickListener {
                item.id?.let { it1 -> anInterface.onLikeClick(it1) }
            }
        }


    }

    fun setItemList(list: ArrayList<CommentResponseItem>) {
        this.commentList.clear()
        this.commentList.addAll(list)
        notifyDataSetChanged()
    }


    /*fun onAudioBtnClick(
        mediaModel: TaskMediaItem,
        progressBar: ProgressBar,
        durationTV: TextView
    ) {
        val mediaPlayerAdp = MediaPlayer()
        mediaPlayerAdp.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayerAdp.setDataSource(mediaModel.mediaUrl)
            mediaPlayerAdp.prepare()

        } catch (e: Exception) {
            Log.e("e:",e.message.toString())
            e.printStackTrace()
        }
        if (mediaPlayerAdp.isPlaying) {
            mediaPlayerAdp.pause()
            mTimer?.cancel()
        } else {
            mediaPlayerAdp.start()
            if (milliSecLeftAdp == mediaModel.duration?.toLong()) {
                mediaModel.duration.toLong()
                    .let { timerStartAdp(it, mediaModel.duration,progressBar,durationTV) }
            } else {
                mediaModel.duration?.let { timerStartAdp(milliSecLeftAdp, it,progressBar,durationTV) }
            }
        }
    }*/


    /* private fun timerStartAdp(timeLengthMilli: Long, totalDuration: Int,progressBar: ProgressBar,
                               durationTV: TextView) {

         milliSecLeftAdp = timeLengthMilli
         val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
         timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
         mTimer = object : CountDownTimer(timeLengthMilli, 1000) {
             override fun onTick(milliTillFinish: Long) {
                 milliSecLeftAdp = milliTillFinish
                 durationTV.text = timeFormatter.format(Date(milliSecLeftAdp))
                 progressBar.progress = totalDuration - milliSecLeftAdp.toInt()
             }

             override fun onFinish() {
                 mTimer?.cancel()
                 milliSecLeftAdp = totalDuration.toLong()
                 progressBar.progress = 0
                 durationTV.text = timeFormatter.format(Date(totalDuration.toLong()))
             }
         }.start()

     }*/

}