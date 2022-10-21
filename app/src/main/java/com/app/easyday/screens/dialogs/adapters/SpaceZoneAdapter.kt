package com.app.easyday.screens.dialogs.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.AttributeSelectionInterface
import com.app.easyday.app.sources.remote.model.AttributeResponse

class SpaceZoneAdapter (
    private val context: Context,
    private var attrList: java.util.ArrayList<AttributeResponse>,
    private var selectedAttrList: java.util.ArrayList<Int>
) : RecyclerView.Adapter<SpaceZoneAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = attrList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_other_filter_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var attrName = itemView.findViewById<TextView>(R.id.childName)
        var selection = itemView.findViewById<ImageView>(R.id.selection)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            attrName.text = attrList[position].attributeName

            if (selectedAttrList.contains(attrList[position].id)) {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_radio))
            } else {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_uncheck_radio))
            }

            itemView.setOnClickListener {
                if (selectedAttrList.contains(attrList[position].id)) {
                    selectedAttrList.remove(attrList[position].id)
                } else {
                    attrList[position].id?.let { it1 -> selectedAttrList.add(it1) }
                }


                notifyDataSetChanged()
            }
        }
    }

    fun getSelectedList():ArrayList<Int>{
        return selectedAttrList
    }

    fun clearList() {
        attrList.clear()
        notifyDataSetChanged()
    }
}