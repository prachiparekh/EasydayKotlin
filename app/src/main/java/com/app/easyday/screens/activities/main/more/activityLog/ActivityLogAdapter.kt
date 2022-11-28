package com.app.easyday.screens.activities.main.more.activityLog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ActivityLogAdapter(private var context: Context,
                         private var listItems: ArrayList<UserActivityResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ACTION_UP = 1
    private val DATAVIEW = 2
    private val MONTHHEADER = 1
    private var invisibleItems: ArrayList<ListItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_log_item, parent, false)
        return ViewHolder(view)

        /*  return if (viewType == DATAVIEW) {

              // view for normal data.
              val view: View = LayoutInflater.from(parent.context)
                  .inflate(R.layout.activity_log_item, parent, false)
              ViewHolder(view)
          } else {

              // view type for month or date header
              val view: View = LayoutInflater.from(parent.context)
                  .inflate(R.layout.date_showing_layout, parent, false)
              ViewHolder1(view)
          }*/
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = holder as ViewHolder
        holder.bind(position)
//        if (holder.itemViewType == DATAVIEW) {
//            val holder = holder as ViewHolder
//            holder.bind(position)
//        } else {
//            val holder = holder as ViewHolder1
//            holder.bind(position)
//        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
    /*override fun getItemViewType(position: Int) =
        if (listItems[position] is ListSection)
            MONTHHEADER
        else
            DATAVIEW

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.title)
        private var titleLayout: RelativeLayout = itemView.findViewById(R.id.titleLayout)

        fun bind(pos: Int) {
            val item = listItems[pos] as ListSection
            var numberOfElements = getNumberOfElementsAfterSection(item, listItems)
            if (numberOfElements == 0) {
                numberOfElements = getNumberOfElementsAfterSection(item, invisibleItems)
            }
            title.text = item.title + " (" + numberOfElements + ")"

            titleLayout.setOnClickListener {
                item.isItemVisible?.let { visibility ->
                    changeListItemsWithVisibility(visibility, pos, item)
                    item.isItemVisible = !visibility
                    notifyDataSetChanged()
                }

            }

        }

        private fun getNumberOfElementsAfterSection(
            item: ListSection,
            list: ArrayList<ListItem>
        ): Int {
            var numberOfElements = 0
            val firstItem = list.find {
                it.sectionName == item.sectionName
            }
            val first = list.indexOf(firstItem)
            val lastItem = list.findLast {
                it.sectionName == item.sectionName
            }
            val last = list.indexOf(lastItem)
            for (position in first..last) {
                if (list[position].sectionName == item.sectionName && list[position] !is ListSection)
                    numberOfElements += 1
            }
            return numberOfElements
        }

        private fun changeListItemsWithVisibility(
            visibility: Boolean,
            pos: Int,
            item: ListSection
        ) {
            if (visibility) {
                var removedItems = 0
                val clonedListItems = ArrayList<ListItem>()
                clonedListItems.addAll(listItems)
                clonedListItems.forEachIndexed { index, listItem ->
                    if (listItem.sectionName == item.title && listItem !is ListSection) {
                        invisibleItems.add(listItems[index - removedItems])
                        listItems.removeAt(index - removedItems)
                        removedItems += 1
                    }
                }
            } else {
                var insertedItems = 1
                var removedItems = 0
                val clonedListItems = ArrayList<ListItem>()
                clonedListItems.addAll(invisibleItems)
                clonedListItems.forEachIndexed { index, invisibleItem ->
                    if (invisibleItem.sectionName == item.title) {
                        listItems.add(pos + insertedItems, invisibleItems[index - removedItems])
                        invisibleItems.removeAt(index - removedItems)
                        insertedItems += 1
                        removedItems += 1
                    }
                }
            }
        }
    }*/

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.comment_tv)
        private var time: TextView = itemView.findViewById(R.id.time)
        private var vLine: View = itemView.findViewById(R.id.v_line)
        private var hLine: View = itemView.findViewById(R.id.h_line)
        private var image: ImageView = itemView.findViewById(R.id.user_image)


        @SuppressLint("StringFormatMatches", "NewApi")
        fun bind(pos: Int) {
            val item = listItems[pos]

            vLine.visibility = View.VISIBLE
            hLine.visibility = View.VISIBLE
            title.text = item.activityText + " " + item.project?.projectName

            val odt = OffsetDateTime.parse(item.createdAt)
            val dtf = DateTimeFormatter.ofPattern("MMM dd,yyyy", Locale.ENGLISH)
            val dtf1 = DateTimeFormatter.ofPattern("HH:MMa", Locale.ENGLISH)


            time.text = context.resources.getString(
                R.string.activity_time,
                dtf.format(odt),
                dtf1.format(odt)
            )

            val options = RequestOptions()
            Glide.with(context)
                .load(item.user?.profileImage)
                .placeholder(R.drawable.ic_comment)
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(image)


        }
    }

    /* @SuppressLint("NotifyDataSetChanged")
     fun clearAll() {
         this.listItems.clear()
         this.invisibleItems.clear()
         notifyDataSetChanged()
     }
     @SuppressLint("NotifyDataSetChanged")
     fun setItems(
         reminders: List<ListItem>?,
         invisibleListItems: List<ListItem>?
     ) {
         this.invisibleItems = ArrayList(invisibleListItems ?: listOf())
         this.listItems = ArrayList(reminders ?: listOf())

         notifyDataSetChanged()
     }*/

}