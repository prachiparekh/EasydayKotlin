package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.annotation.SuppressLint
import android.content.Context
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

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            val item = commentList[position]
            commenterName.text = item.user?.fullname

            val odt = OffsetDateTime.parse(item.createdAt)
            val dtf = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

            timeTV.text = dtf.format(odt)
            commentTV.text = item.comment


        }
    }


}