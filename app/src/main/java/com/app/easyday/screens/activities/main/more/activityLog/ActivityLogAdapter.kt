package com.app.easyday.screens.activities.main.more.activityLog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.ListItem
import com.app.easyday.app.sources.local.model.ListSection
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ActivityLogAdapter(
    private var context: Context,
    private var listItems: ArrayList<ListItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val DATAVIEW = 2
    private val MONTHHEADER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DATAVIEW) {
            // view for normal data.
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_log_item, parent, false)
            ViewHolder(view)
        } else {
            // view type for month or date header
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.date_showing_layout, parent, false)
            ViewHolder1(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder.itemViewType == DATAVIEW) {
            val holder = viewHolder as ViewHolder
            holder.bind(position)
        } else {
            val holder = viewHolder as ViewHolder1
            holder.bind(position)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.comment_tv)
        private var time: TextView = itemView.findViewById(R.id.time)
        private var vLine: View = itemView.findViewById(R.id.v_line)
        private var hLine: View = itemView.findViewById(R.id.h_line)
        private var image: ImageView = itemView.findViewById(R.id.user_image)

        @SuppressLint("StringFormatMatches", "NewApi")
        fun bind(pos: Int) {
            val item = listItems[pos] as UserActivityResponse

            vLine.visibility = View.VISIBLE
            hLine.visibility = View.VISIBLE
            if (item.type == 1 || item.type == 5)
                title.text = item.activityText + " " + item.task?.title

            val oldFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            oldFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var value: Date? = null
            var dueDateAsNormal = ""
            try {
                value = oldFormatter.parse(item.createdAt)
                val newFormatter = SimpleDateFormat("MMM dd,yyyy • hh:mma")
                newFormatter.timeZone = TimeZone.getDefault()
                dueDateAsNormal = newFormatter.format(value)
            } catch (e: ParseException) {
                e.printStackTrace()
            }


            time.text = dueDateAsNormal

            val options = RequestOptions()
            Glide.with(context)
                .load(item.userActivityType?.image)
                .placeholder(R.drawable.ic_comment)
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(image)
        }
    }

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var titleDate: TextView = itemView.findViewById(R.id.today)

        @SuppressLint("StringFormatMatches", "NewApi")
        fun bind(pos: Int) {
            val item = listItems[pos] as ListSection
            titleDate.text = item.title
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(
        reminders: List<ListItem>?
    ) {
        this.listItems = ArrayList(reminders ?: listOf())
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearAll() {
        this.listItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) =
        if (listItems[position] is ListSection)
            MONTHHEADER
        else
            DATAVIEW

}