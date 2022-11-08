package com.app.easyday.app.sources.remote.model

import com.app.easyday.screens.activities.main.more.activityLog.ListItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserActivityResponse(

	@field:SerializedName("activity_text")
	val activityText: String? = null,

	@field:SerializedName("receiver")
	val receiver: Any? = null,

	@field:SerializedName("receiver_id")
	val receiverId: Any? = null,

	@field:SerializedName("project")
	val project: ProjectRespModel? = null,

	@field:SerializedName("task_id")
	val taskId: Int? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("task_comment")
	val taskComment: Any? = null,

	@field:SerializedName("task_comment_id")
	val taskCommentId: Any? = null,

	@field:SerializedName("task")
	val task: TaskResponse? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: UserModel? = null,

	@field:SerializedName("user_activity_type")
	val userActivityType: UserActivityType? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
): ListItem(null, null), Serializable {
	override fun toString(): String {
		return "UserActivityResponse(activityText=$activityText, receiver=$receiver, receiverId=$receiverId, project=$project, taskId=$taskId, type=$type, createdAt=$createdAt" +
				", taskComment=$taskComment, taskCommentId=$taskCommentId, task=$task, userId=$userId, projectId=$projectId, id=$id" +
				", user=$user" + ", userActivityType=$userActivityType, updatedAt=$updatedAt)"
	}
}


data class UserActivityType(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)