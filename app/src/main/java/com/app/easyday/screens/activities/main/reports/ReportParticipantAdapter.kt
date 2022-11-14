package com.app.easyday.screens.activities.main.reports

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.ReportParticipantsModel
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions

class ReportParticipantAdapter(
    private val context: Context,
    private var contactList: ArrayList<ReportParticipantsModel>,
    private var isLineFragment: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val Line = 1
    private val Chart = 2

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun getItemCount(): Int = contactList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == Line && isLineFragment) {

            // view for normal data.
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.report_participant_layout, parent, false)
            ViewHolder(view)

        } else {

            // view type for month or date header
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.report_participant_chart_layout, parent, false)
            ViewHolder1(view)
        }
    }

    override fun getItemViewType(position: Int) =
        if (isLineFragment)
            Line
        else
            Chart

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Line) {
            val holder = holder as ViewHolder
            holder.bind(position)
            isLineFragment = true
        } else {
            val holder = holder as ViewHolder1
            holder.bind(position)
            isLineFragment = false
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar = itemView.findViewById<ImageView>(R.id.profile)
        val assignedCount = itemView.findViewById<TextView>(R.id.task_assigned_count)
        val participantName = itemView.findViewById<TextView>(R.id.participantTv)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            participantName.text = contactList[position].fullname
            assignedCount.text = contactList[position].taskCount.toString()
            val options = RequestOptions()
            avatar.clipToOutline = true
            Glide.with(context)
                .load(contactList[position].profileImage)
                .error(context.resources.getDrawable(R.drawable.ic_profile_circle))
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(avatar)

        }
    }

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar = itemView.findViewById<ImageView>(R.id.profile_1)
        val assignedCount = itemView.findViewById<TextView>(R.id.task_assigned_count_1)
        val participantName = itemView.findViewById<TextView>(R.id.participantTv_1)
        val progressbar = itemView.findViewById<ProgressBar>(R.id.count_progress)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            participantName.text = contactList[position].fullname
            assignedCount.text = contactList[position].taskCount.toString()
            val options = RequestOptions()
            avatar.clipToOutline = true
            Glide.with(context)
                .load(contactList[position].profileImage)
                .error(context.resources.getDrawable(R.drawable.ic_profile_circle))
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(avatar)

        }
    }

}