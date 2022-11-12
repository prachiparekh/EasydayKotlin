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
import com.app.easyday.app.sources.remote.model.ReportTagResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_line.*

class TagAdapter(
    private val context: Context,
    private var tagList: ArrayList<ReportTagResponse>,
    private var isLineFragment: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val Line = 1
    private val Chart = 2

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun getItemCount(): Int = tagList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = inflater.inflate(R.layout.item_tag_line_report, parent, false)
//        return ViewHolder(view)

        return if (viewType == Line && isLineFragment) {

            // view for normal data.
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tag_line_report, parent, false)
            ViewHolder(view)

        } else {

            // view type for month or date header
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tag_chart_report, parent, false)
            ViewHolder1(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.bind(position)
        if (holder.itemViewType == Line) {
            val holder = holder as TagAdapter.ViewHolder
            holder.bind(position)
            isLineFragment = true
        } else {
            val holder = holder as TagAdapter.ViewHolder1
            holder.bind(position)
            isLineFragment = false
        }
    }

    override fun getItemViewType(position: Int) =
        if (isLineFragment)
            Line
        else
            Chart

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagName = itemView.findViewById<TextView>(R.id.R_tagName)
        val tagCount = itemView.findViewById<TextView>(R.id.tag_count)

        @SuppressLint("StringFormatInvalid")
        fun bind(position: Int) {
            val tagsName = tagList[position].tagName
            tagCount.text = tagList[position].projectTagsCount.toString()

            tagName.text = context.resources.getString(R.string.tag_name, tagsName)
        }
    }

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagName = itemView.findViewById<TextView>(R.id.tag_name_1)
        val tagCount = itemView.findViewById<TextView>(R.id.tag_count_1)

        @SuppressLint("StringFormatInvalid")
        fun bind(position: Int) {
            val tagsName = tagList[position].tagName
            tagCount.text = tagList[position].projectTagsCount.toString()

            tagName.text = tagsName
        }
    }
}