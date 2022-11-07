package com.app.easyday.screens.activities.main.more.activityLog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ActivityLogAdapter(private var context: Context,
    private var listItems: ArrayList<UserActivityResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

//    private val ACTION_UP = 1
//    private val DATAVIEW = 2
//    private val MONTHHEADER = 1
//    private var invisibleItems: ArrayList<ListItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
          val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_log_item, parent, false)
        return ViewHolder(view)

//        if (viewType == DATAVIEW) {
//
//            // view for normal data.
//            val view: View = LayoutInflater.from(parent.context)
//                .inflate(R.layout.activity_log_item, parent, false)
//            ViewHolder(view)
//        } else {
//
//            // view type for month or date header
//            val view: View = LayoutInflater.from(parent.context)
//                .inflate(R.layout.date_showing_layout, parent, false)
//            ViewHolder1(view)
//        }
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
//    override fun getItemViewType(position: Int) =
//        if (listItems[position] is ListSection)
//            MONTHHEADER
//        else
//            DATAVIEW


   /* inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.title)
        private var titleLayout: LinearLayout = itemView.findViewById(R.id.titleLayout)

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


        @SuppressLint("StringFormatMatches", "NewApi")
        fun bind(pos: Int) {
            val item = listItems[pos] as UserActivityResponse
//            item.completed?.let {
//                if (it) {
//                    strikeTrough.visibility = View.VISIBLE
                    title.setTypeface(null, Typeface.ITALIC)
                    title.setTextColor(Color.GRAY)
//                } else {
//                    strikeTrough.visibility = View.GONE
//                    title.setTypeface(null, Typeface.BOLD)
//                    title.setTextColor(Color.parseColor("#303065"))
//                }
//            }
            title.text = item.activityText + " " + item.project?.projectName
//            time.text = item.createdAt
//            val dot = context.resources.getString(R.string.dot)
//            val customFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd/yyyy 'at' hh:mma z")
            val odt = OffsetDateTime.parse(item.createdAt)
            val dtf = DateTimeFormatter.ofPattern("MMM dd,yyyy", Locale.ENGLISH)
            val dtf1 = DateTimeFormatter.ofPattern("HH:MMa", Locale.ENGLISH)
            Log.e("title",  dtf.format(odt).toString())
            time.text = context.resources.getString(R.string.activity_time, dtf.format(odt),  dtf1.format(odt))


            /*notes.text = item.notes
            if (item.tags?.isNotEmpty() == true) {
                val firstTag = item.tags[0]
                val tagCatalog = firstTag?.let { CatalogUtils.findById(tagsCatalog, it) }
                val tagname = tagCatalog?.name
                if (tagname.isNullOrEmpty()) {
                    tag.isVisible = false
                    tagCircle.isInvisible = true
                } else {
                    tag.isVisible = true
                    tagCircle.isVisible = true
                    tag.text = tagname

                    tagIndicator.setColorFilter(
                        Color.parseColor("#" + tagCatalog.color),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tagCircle.setColorFilter(
                        Color.parseColor("#" + tagCatalog.color),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )

                }
            } else {
                tag.isVisible = false
                tagCircle.isInvisible = true
            }*/

        }
    }

}