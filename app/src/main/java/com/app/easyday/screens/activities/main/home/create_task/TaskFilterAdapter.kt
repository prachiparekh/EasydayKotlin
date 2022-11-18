package com.app.easyday.screens.activities.main.home.create_task

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterTypeInterface


class TaskFilterAdapter(
    private val context: Context,
    private var filterList: ArrayList<String>,
    private var priorityList: ArrayList<String>,
    isSelected: Boolean,
    private var drawableList: ArrayList<Drawable>,
    val filterTypeInterface: FilterTypeInterface
) : RecyclerView.Adapter<TaskFilterAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var selectedPosition = -1
    var flag = false

    override fun getItemCount(): Int = filterList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_task_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterName = itemView.findViewById<TextView>(R.id.label)
        val close = itemView.findViewById<ImageView>(R.id.close)
        val childRV = itemView.findViewById<RecyclerView>(R.id.childRV)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            filterName.text = filterList[position]
            filterName.setCompoundDrawablesWithIntrinsicBounds(
                drawableList[position],
                null,
                null,
                null
            )

            itemView.setOnClickListener {
                selectedPosition = position
                when (position) {
                    5 -> {
                        //DueDate

                        filterTypeInterface.onFilterDueDateClick(filterName.text.toString())

                    }
                    2 -> {
//                        RedFlag
//                        No code place here bcz notify sends another onFilterFlagClick listener below
                    }
                    else -> {
                        filterTypeInterface.onFilterTypeClick(position)
                    }
                }
                notifyDataSetChanged()
            }

            if (selectedPosition != -1) {
                when (selectedPosition) {
                    0 -> {
                        priorityChanges(position)
                    }
                    1, 3, 4, 5 -> {
                        elseChanges(position)
                    }
                    2 -> {
                        flagChanges(position)
                    }
                }
                textChanges(position)
            } else {
                onClose()
            }

            close.setOnClickListener {
                onClose()
            }
        }

        private fun onClose() {
            close.isVisible = false
            filterName.setTextColor(context.resources.getColor(R.color.light_white))
            setTextViewDrawableColor(filterName, R.color.light_white)
            if (position == 0)
                childRV.adapter = null
        }

        private fun priorityChanges(currentPosition: Int) {
            if (currentPosition == selectedPosition) {
                childRV.adapter =
                    FilterChildAdapter(context, priorityList, filterTypeInterface)
                close.isVisible = true
            } else {
                childRV.adapter = null
                close.isVisible = false
            }
        }

        private fun elseChanges(currentPosition: Int) {
            if (currentPosition == selectedPosition) {
                childRV.adapter = null
                close.isVisible = true
            } else {
                childRV.adapter = null
                close.isVisible = false
            }
        }

        private fun flagChanges(currentPosition: Int) {
            if (currentPosition == selectedPosition) {

                childRV.adapter = null
                close.isVisible = false
            } else {
                childRV.adapter = null
                close.isVisible = false
                filterName.setTextColor(context.resources.getColor(R.color.light_white))
                setTextViewDrawableColor(filterName, R.color.light_white)
            }
        }

        private fun textChanges(currentPosition: Int) {
            if (filterList[currentPosition] != context.resources.getString(R.string.f_red_flag)) {
                if (currentPosition == selectedPosition) {
                    filterName.setTextColor(context.resources.getColor(R.color.white))
                    setTextViewDrawableColor(filterName, R.color.white)
                } else {
                    filterName.setTextColor(context.resources.getColor(R.color.light_white))
                    setTextViewDrawableColor(filterName, R.color.light_white)
                }
            } else {
                if (!flag && filterList[selectedPosition] == context.resources.getString(R.string.f_red_flag)) {
                    flag = true
                    filterName.setTextColor(context.resources.getColor(R.color.white))
                    setTextViewDrawableColor(filterName, R.color.red)
                    filterTypeInterface.onFilterFlagClick(flag)
                } else {
                    flag = false
                    filterName.setTextColor(context.resources.getColor(R.color.light_white))
                    setTextViewDrawableColor(filterName, R.color.light_white)
                    filterTypeInterface.onFilterFlagClick(flag)
                }
            }
        }
    }


    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(textView.context, color),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }

    fun closeFilter() {
        selectedPosition = -1
        notifyDataSetChanged()
    }

    fun dueDateFilter(dateStr: String) {
        filterList[5] = dateStr
        notifyItemChanged(5)
    }

    fun priorityChanged() {
//        childRV.adapter = null
        selectedPosition = -1
        notifyItemChanged(0)

    }

}