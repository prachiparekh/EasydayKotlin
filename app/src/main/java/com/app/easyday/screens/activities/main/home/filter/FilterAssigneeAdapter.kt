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
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.screens.activities.main.home.filter.FilterFragment.Companion.filterAssigneeList
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions

class FilterAssigneeAdapter(
    private val context: Context,
    private var filterChildList: java.util.ArrayList<ContactModel>,
    private var selectedChildPositionList: java.util.ArrayList<Int>
) : RecyclerView.Adapter<FilterAssigneeAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = filterChildList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_participant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val selection = itemView.findViewById<ImageView>(R.id.selection)
        val participantName = itemView.findViewById<TextView>(R.id.participantName)
        val avatar = itemView.findViewById<ImageView>(R.id.avatar)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            participantName.text = filterChildList[position].name

            val options = RequestOptions()
            avatar.clipToOutline = true
            if (filterChildList[position].photoURI != "null") {
                Glide.with(context)
                    .load(filterChildList[position].photoURI)
                    .error(context.resources.getDrawable(R.drawable.ic_profile_circle))
                    .apply(
                        options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(avatar)
            }else{
                Glide.with(context)
                    .load(context.resources.getDrawable(R.drawable.ic_profile_circle))
                    .apply(
                        options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(avatar)
            }

            if (selectedChildPositionList.contains(filterChildList[position].id?.toInt())) {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_radio))
            } else {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_uncheck_radio))
            }

            itemView.setOnClickListener {
                if (!selectedChildPositionList.contains(filterChildList[position].id?.toInt())) {
                    filterChildList[position].id?.let { it1 -> selectedChildPositionList.add(it1.toInt()) }
                    selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_radio))

                    filterChildList[position].id?.toInt()?.let { it1 -> filterAssigneeList.add(it1) }
                } else {
                    selectedChildPositionList.remove(filterChildList[position].id?.toInt())
                    selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_uncheck_radio))

                    filterAssigneeList.remove(filterChildList[position].id?.toInt())
                }
            }
        }
    }

    fun getMultiplePositionList(): ArrayList<Int> {
        return selectedChildPositionList
    }
}