package com.app.easyday.app.sources.local.model

data class ListSection(
    val title: String,
    val code: String,
    val isToday: Boolean,
    val isPastSection: Boolean
) : ListItem(null) {
    override fun toString(): String {
        return "ListSection(title='$title', code='$code', isToday=$isToday, isPastSection=$isPastSection)"
    }
}
