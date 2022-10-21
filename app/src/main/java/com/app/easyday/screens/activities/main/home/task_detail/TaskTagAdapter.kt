package com.app.easyday.screens.activities.main.home.task_detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.TaskAttributeResponse

class TaskTagAdapter (
    private val context: Context,
    private var tagList: java.util.ArrayList<TaskAttributeResponse>
) : RecyclerView.Adapter<TaskTagAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = tagList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mainCard = itemView.findViewById<CardView>(R.id.mainCard)
        var tagName = itemView.findViewById<TextView>(R.id.tagName)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            tagName.text = tagList[position].projectAttribute?.attributeName
            tagName.setTextColor(context.resources.getColor(R.color.sky_blue))
            mainCard.setCardBackgroundColor(context.resources.getColor(R.color.light_sky_blue))

        }
    }

}