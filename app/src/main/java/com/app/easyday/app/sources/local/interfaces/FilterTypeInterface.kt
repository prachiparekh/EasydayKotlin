package com.app.easyday.app.sources.local.interfaces

import android.widget.TextView

interface FilterTypeInterface {
    fun onFilterTypeClick(position: Int)
    fun onFilterSingleChildClick(
        childList: ArrayList<String>,
        childLabel: TextView,
        childPosition: Int
    )

    fun onFilterFlagClick(redFlag: Boolean)
    fun onFilterDueDateClick(dateStr: String)
}