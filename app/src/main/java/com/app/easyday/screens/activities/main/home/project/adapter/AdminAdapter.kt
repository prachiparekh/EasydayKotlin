package com.app.easyday.screens.activities.main.home.project.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.toLowerCase
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.ContactModel

class AdminAdapter(
    private val context: Context,
    private var contactList: ArrayList<ContactModel>
) : RecyclerView.Adapter<AdminAdapter.ViewHolder>(), Filterable {

    private var selectedContactList = ArrayList<ContactModel>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var filterData: ArrayList<ContactModel> = contactList

    override fun getItemCount(): Int = filterData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_participant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar = itemView.findViewById<ImageView>(R.id.avatar)
        val selection = itemView.findViewById<ImageView>(R.id.selection)
        val briefcase = itemView.findViewById<ImageView>(R.id.briefcase)
        val participantName = itemView.findViewById<TextView>(R.id.participantName)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            val item=filterData[position]
            participantName.text = item.name

            val options = RequestOptions()
            avatar.clipToOutline = true
            if (item.photoURI != "null") {
                Glide.with(context)
                    .load(item.photoURI)
                    .error(context.resources.getDrawable(R.drawable.ic_profile_circle))
                    .apply(
                        options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(avatar)
            } else {
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

            if (selectedContactList.contains(item)) {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_radio))
                briefcase.isVisible = true
            } else {
                selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_uncheck_radio))
                briefcase.isVisible = false
            }

            itemView.setOnClickListener {
                if (!selectedContactList.contains(item)) {
                    selectedContactList.add(item)
                    selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_radio))
                    briefcase.isVisible = true
                    item.role = context.resources.getString(R.string.admin_role)
                } else {
                    selectedContactList.remove(item)
                    selection.setImageDrawable(context.resources.getDrawable(R.drawable.ic_uncheck_radio))
                    briefcase.isVisible = false
                    item.role =
                        context.resources.getString(R.string.user_role)
                }
            }
        }
    }

    fun getList(): ArrayList<ContactModel> {
        return selectedContactList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filteredList1: MutableList<ContactModel> = ArrayList()
                if (charSequence.toString().isEmpty()) {
                    filteredList1.addAll(contactList)
                } else {
                    for (location in contactList) {
                        if (location.name?.toLowerCase()
                                ?.contains(charSequence.toString().toLowerCase()) == true
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
                filterData = filterResults.values as ArrayList<ContactModel>
                notifyDataSetChanged()
            }
        }
    }

}