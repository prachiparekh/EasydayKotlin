package com.app.easyday.screens.activities.main.home.task_detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.TaskParticipantsItem
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions

class TaskAssignedAdapter(
    private val context: Context,
    private var contactList: ArrayList<TaskParticipantsItem>
) : RecyclerView.Adapter<TaskAssignedAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun getItemCount(): Int = contactList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.assigned_participant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar = itemView.findViewById<ImageView>(R.id.avatar)
        val selection = itemView.findViewById<ImageView>(R.id.selection)
        val participantName = itemView.findViewById<TextView>(R.id.participantName)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            participantName.text = contactList[position].user?.fullname


            val options = RequestOptions()
            avatar.clipToOutline = true
            if (contactList[position].user?.profileImage != null) {
                Glide.with(context)
                    .load(contactList[position].user?.profileImage)
                    .error(context.resources.getDrawable(R.drawable.ic_profile_circle))
                    .apply(
                        options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(avatar)
            } else {
                Glide.with(context)
                    .load(context.resources.getDrawable(R.drawable.ic_profile_circle))
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

}