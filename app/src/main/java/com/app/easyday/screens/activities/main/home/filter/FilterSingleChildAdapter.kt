package com.app.easyday.screens.activities.main.home.filter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterDueDate
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterPriority
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterRedFlag
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterTaskStatus

class FilterSingleChildAdapter(
    private val context: Context,
    private var filterChildList: java.util.ArrayList<String>,
    val filterType: String
) : RecyclerView.Adapter<FilterSingleChildAdapter.ViewHolder>() {

    var selectedPosition = -1
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = filterChildList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_other_filter_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childName = itemView.findViewById<TextView>(R.id.childName)
        val selection = itemView.findViewById<ImageView>(R.id.selection)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            childName.text = filterChildList[position]

            when (filterType) {
                "TASKSTATUS" -> selectedPosition = filterTaskStatus ?: -1
                "PRIORITY" -> selectedPosition = filterPriority ?: -1
                "REDFLAG" -> selectedPosition = filterRedFlag ?: -1
                "DUEDATE" -> selectedPosition = filterDueDate ?: -1
            }

            if (selectedPosition == position) {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_radio))
            } else {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_uncheck_radio))
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousPosition)
                when (filterType) {
                    "TASKSTATUS" -> filterTaskStatus = position
                    "PRIORITY" -> filterPriority = position
                    "REDFLAG" -> filterRedFlag = position
                    "DUEDATE" -> filterDueDate = position
                }
            }
        }
    }

}