package com.app.easyday.screens.activities.main.home.task_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.TaskInterfaceClick
import com.app.easyday.app.sources.remote.model.TaskAttributeResponse
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedColor
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TaskAdapter(
    private val context: Context,
    private var taskList: ArrayList<TaskResponse>,
    val anInterfaceClick: TaskInterfaceClick
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {



    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = taskList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taskTitle = itemView.findViewById<TextView>(R.id.taskTitle)
        val mediaPager = itemView.findViewById<ViewPager2>(R.id.mediaPager)
        val mDate = itemView.findViewById<TextView>(R.id.mDate)
        val mDescription = itemView.findViewById<TextView>(R.id.mDescription)
        val tagRV = itemView.findViewById<RecyclerView>(R.id.tagRV)
        val dots_indicator = itemView.findViewById<TabLayout>(R.id.dots_layout)
        val flag = itemView.findViewById<ImageView>(R.id.flag)
        val priority = itemView.findViewById<ImageView>(R.id.priority)
        val projectIcon = itemView.findViewById<ImageView>(R.id.projectIcon)
        val discussion = itemView.findViewById<TextView>(R.id.discussion)
        val taskStatus = itemView.findViewById<TextView>(R.id.taskStatus)
        val edit = itemView.findViewById<ImageView>(R.id.edit)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            val item = taskList[position]
            taskTitle.text = item.title
//            mDescription.text = item.title

            val odt = OffsetDateTime.parse(item.createdAt)
            val dtf = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

            mDate.text = dtf.format(odt)
            mDescription.text = item.description

            if (item.redFlag == true) {
                flag.setImageDrawable(context.resources.getDrawable(R.drawable.ic_flaged))
            } else {
                flag.setImageDrawable(context.resources.getDrawable(R.drawable.ic_flag))
            }

            when (item.priority) {
                0 -> priority.isVisible = false
                1 -> priority.setImageDrawable(context.resources.getDrawable(R.drawable.ic_low))
                2 -> priority.setImageDrawable(context.resources.getDrawable(R.drawable.ic_normal))
                3 -> priority.setImageDrawable(context.resources.getDrawable(R.drawable.ic_urgent))
            }

            ImageViewCompat.setImageTintList(
                projectIcon, ColorStateList.valueOf(
                    Color.parseColor(
                        selectedColor
                    )
                )
            )

            var mediaAdapter: TaskMediaAdapter? = null
            mediaAdapter = TaskMediaAdapter(
                context,
                onItemClick = { isImg, uri ->
                    if (!isImg) {
                        val play = Intent(Intent.ACTION_VIEW, uri.toUri())
                        play.setDataAndType(
                            uri.toUri(),
                            "video/mp4"
                        )
                        context.startActivity(play)
                    }
                },

                )

            mediaPager.apply {
                adapter = mediaAdapter.apply { submitList(item.taskMedia) }
            }

            TabLayoutMediator(dots_indicator, mediaPager) { tab, position ->

            }.attach()

            tagRV.adapter = TaskTagAdapter(
                context,
                item.taskTags as ArrayList<TaskAttributeResponse>
            )

            edit.setOnClickListener {
                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
            }
            itemView.setOnClickListener {
                position.let { it1 ->
                    anInterfaceClick.onTaskClick(item)
                }
            }

            discussion.setOnClickListener {
                item.id.let { it1 ->
                    if (it1 != null) {
                        anInterfaceClick.onDiscussionClick(it1)
                    }
                }
            }
            taskStatus.setOnClickListener {
            }

        }
    }


}