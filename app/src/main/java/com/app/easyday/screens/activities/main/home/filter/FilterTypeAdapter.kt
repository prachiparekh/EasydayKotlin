package com.app.easyday.screens.activities.main.home.filter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterTypeInterface

class FilterTypeAdapter(
    private val context: Context,
    private var filterList: java.util.ArrayList<String>,
    val filterTypeInterface: FilterTypeInterface
) : RecyclerView.Adapter<FilterTypeAdapter.ViewHolder>() {


    var selectedPosition = 0
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = filterList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_filter_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterName = itemView.findViewById<TextView>(R.id.filterName)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            filterName.text = filterList[position]

            if (selectedPosition == position) {
                itemView.setBackgroundColor(context.resources.getColor(R.color.white))
            } else {
                itemView.setBackgroundColor(context.resources.getColor(R.color.bg_white))
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousPosition)
                position.let { it1 -> filterTypeInterface.onFilterTypeClick(it1) }
            }
        }
    }
}