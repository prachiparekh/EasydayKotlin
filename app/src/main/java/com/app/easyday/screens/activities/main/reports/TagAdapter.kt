package com.app.easyday.screens.activities.main.reports

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.ContactModel
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions

class TagAdapter (
    private val context: Context,
    private var tagList: ArrayList<String>
) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {


    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun getItemCount(): Int = tagList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_tag_line_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagName = itemView.findViewById<TextView>(R.id.tagName)

        fun bind(position: Int) {
            tagName.text = tagList[position]
        }
    }
}