package com.app.easyday.screens.dialogs.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.AttributeSelectionInterface
import com.app.easyday.app.sources.remote.model.AttributeResponse

class TagsAdapter(
    private val context: Context,
    private var tagList: java.util.ArrayList<AttributeResponse>,
    private var selectedTagList: java.util.ArrayList<Int>
) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

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

            tagName.text = tagList[position].attributeName

            if (selectedTagList.contains(tagList[position].id)) {
                mainCard.setCardBackgroundColor(context.resources.getColor(R.color.light_sky_blue))
                tagName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_blue_circle, 0)
                tagName.setTextColor(context.resources.getColor(R.color.sky_blue))
            } else {
                mainCard.setCardBackgroundColor(context.resources.getColor(R.color.light_gray))
                tagName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                tagName.setTextColor(context.resources.getColor(R.color.navy_blue))
            }

            itemView.setOnClickListener {
                if (selectedTagList.contains(tagList[position].id)) {
                    selectedTagList.remove(tagList[position].id)
                } else {
                    tagList[position].id?.let { it1 -> selectedTagList.add(it1) }
                }

                notifyDataSetChanged()
            }
        }
    }

    fun getSelectedTagList(): ArrayList<Int> {
        return selectedTagList
    }

    fun clearList() {
        tagList.clear()
        notifyDataSetChanged()
    }
}