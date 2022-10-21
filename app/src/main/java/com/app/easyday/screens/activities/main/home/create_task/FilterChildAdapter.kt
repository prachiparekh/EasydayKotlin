package com.app.easyday.screens.activities.main.home.create_task

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterTypeInterface

class FilterChildAdapter(
    private val context: Context,
    private var priorityList: java.util.ArrayList<String>,
    val filterTypeInterface: FilterTypeInterface
) : RecyclerView.Adapter<FilterChildAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = priorityList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_task_child_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childLabel = itemView.findViewById<TextView>(R.id.childLabel)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            childLabel.text = priorityList[position]

            itemView.setOnClickListener {
                position.let { it1 ->
                    filterTypeInterface.onFilterSingleChildClick(
                        priorityList,
                        it1
                    )
                }
            }
        }
    }
}