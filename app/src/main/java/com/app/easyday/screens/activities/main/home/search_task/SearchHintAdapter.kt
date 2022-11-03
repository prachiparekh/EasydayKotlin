package com.app.easyday.screens.activities.main.home.search_task

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.SearchHintInterface

class SearchHintAdapter(
    private val context: Context,
    private var hintList: java.util.ArrayList<String>,
    var anInterface: SearchHintInterface
) : RecyclerView.Adapter<SearchHintAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = hintList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectName = itemView.findViewById<TextView>(R.id.projectName)
        val description = itemView.findViewById<TextView>(R.id.description)


        @SuppressLint("NewApi")
        fun bind(position: Int) {

            projectName.text = hintList[position]
//            description.text = hintList[position]

            itemView.setOnClickListener {
                anInterface.onHintClick(hintList[position])
            }
        }
    }

    fun addItem(title: String) {
        hintList.add(0, title)
        notifyItemInserted(0)
    }

}