package com.app.easyday.screens.activities.main.more.feedback

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FeedBackTagInterfaceClick

import kotlin.collections.ArrayList

class FeedTagsAdapter (private var context: Context, private var listItems: ArrayList<String>, val tagClick: FeedBackTagInterfaceClick
) : RecyclerView.Adapter<FeedTagsAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.feedback_tag_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedTagsAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.tag_TV)
        private var title_bg: RelativeLayout = itemView.findViewById(R.id.tag_RL)



        @SuppressLint("StringFormatMatches", "NewApi")
        fun bind(pos: Int) {

            val item = listItems[pos]
            title.text = listItems[pos]


//            itemView.setOnClickListener {
//                title_bg.setOnClickListener {
//                    title_bg.isSelected = !title_bg.isSelected
//
//                    pos.let { it1 ->
//                        tagClick.onTagClick(it1)
//                    }
//                }
//            }
            title_bg.setOnClickListener {
                title_bg.isSelected = !title_bg.isSelected

                pos.let { it1 ->
                    tagClick.onTagClick(it1, title_bg)
                }
            }

        }
    }


}