package com.app.easyday.screens.activities.main.home.search_task

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.TaskInterfaceClick
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import java.util.*

class SearchAdapter(
    private val context: Context,
    private var taskList: java.util.ArrayList<TaskResponse>,
    val anInterface: TaskInterfaceClick
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(), Filterable {

    var filterData: ArrayList<TaskResponse> = taskList
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = filterData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_search_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectName = itemView.findViewById<TextView>(R.id.projectName)
        val description = itemView.findViewById<TextView>(R.id.description)
        val priority = itemView.findViewById<TextView>(R.id.priority)
        val mImage = itemView.findViewById<ImageView>(R.id.mImage)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            projectName.text = filterData[position].title
            description.text = filterData[position].description

            var taskImg: String? = null
            val mediaList = filterData[position].taskMedia
            mediaList?.indices?.forEach { i ->
                if (mediaList[i]?.mediaUrl?.endsWith("png") == true || mediaList[i]?.mediaUrl?.endsWith(
                        "jpg"
                    ) == true
                    ||
                    mediaList[i]?.mediaUrl?.endsWith("jpeg") == true
                )
                    taskImg = mediaList[i]?.mediaUrl
            }

            val options = RequestOptions()
            mImage.clipToOutline = true
            Glide.with(context)
                .load(taskImg)
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(mImage)

            when (taskList[position].priority) {
                0 -> {
                    priority.isVisible = false
                }
                1 -> {
                    priority.text = context.resources.getString(R.string.low_p)
                    priority.setTextColor(context.resources.getColor(R.color.green))
                    priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_low, 0, 0, 0)
                }
                2 -> {
                    priority.text = context.resources.getString(R.string.normal_p)
                    priority.setTextColor(context.resources.getColor(R.color.yellow))
                    priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_normal, 0, 0, 0)
                }
                3 -> {
                    priority.text = context.resources.getString(R.string.urgent_p)
                    priority.setTextColor(context.resources.getColor(R.color.red))
                    priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_urgent, 0, 0, 0)
                }
            }

            itemView.setOnClickListener {
                anInterface.onTaskClick(filterData[position])
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filteredList1: MutableList<TaskResponse> = ArrayList()
                if (charSequence.toString().isEmpty()) {
                    filteredList1.addAll(taskList)
                } else {
                    for (location in taskList) {
                        if (location.title?.lowercase(Locale.getDefault())
                                ?.contains(
                                    charSequence.toString().lowercase(Locale.getDefault())
                                ) == true
                        ) {
                            filteredList1.add(location)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList1
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filterData = filterResults.values as ArrayList<TaskResponse>
                anInterface.onSearchResult(filterData.size)
                notifyDataSetChanged()
            }
        }
    }


}