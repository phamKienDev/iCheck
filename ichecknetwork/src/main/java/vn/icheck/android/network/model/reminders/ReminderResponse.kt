package vn.icheck.android.network.model.reminders

import com.google.gson.annotations.SerializedName

data class ReminderResponse(

	@field:SerializedName("entityType")
	val entityType: String? = null,

	@field:SerializedName("icon")
	val icon: String? = null,

	@field:SerializedName("redirectPath")
	val redirectPath: String? = null,

	@field:SerializedName("entityId")
	val entityId: Int? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("readCount")
	val readCount: Int? = null,

	@field:SerializedName("priority")
	val priority: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("pushToUserId")
	val pushToUserId: Int? = null,

	@field:SerializedName("obj")
	val obj: String? = null,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("startTime")
	val startTime: String? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("type")
	val type:String? = null
)
