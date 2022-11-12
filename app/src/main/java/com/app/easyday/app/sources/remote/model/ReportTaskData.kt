package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class ReportTasksData(

    @field:SerializedName("normal")
    val normal: Int? = null,

    @field:SerializedName("in_progress")
    val inProgress: Int? = null,

    @field:SerializedName("in_review")
    val inReview: Int? = null,

    @field:SerializedName("reopened")
    val reopened: Int? = null,

    @field:SerializedName("total_task")
    val totalTask: Int? = null,

    @field:SerializedName("non_completed_count")
    val nonCompletedCount: Int? = null,

    @field:SerializedName("low")
    val low: Int? = null,

    @field:SerializedName("not_flaged")
    val notFlaged: Int? = null,

    @field:SerializedName("completed")
    val completed: Int? = null,

    @field:SerializedName("urgent")
    val urgent: Int? = null,

    @field:SerializedName("red_flaged")
    val redFlaged: Int? = null
) {
    override fun toString(): String {
        return "TasksData(normal=$normal, inProgress=$inProgress, inReview=$inReview, reopened=$reopened, totalTask=$totalTask, nonCompletedCount=$nonCompletedCount, low=$low, notFlaged=$notFlaged, completed=$completed, urgent=$urgent, redFlaged=$redFlaged)"
    }
}
