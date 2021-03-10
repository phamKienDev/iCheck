package vn.icheck.android.model.posts

import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.ICPost

data class PostResponse(

	@field:SerializedName("data")
	val data: PostData? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class PostItem(

	@field:SerializedName("expressiveCount")
	val expressiveCount: Int? = null,

	@field:SerializedName("targetId")
	val targetId: Int? = null,

	@field:SerializedName("targetType")
	val targetType: String? = null,

	@field:SerializedName("involveType")
	val involveType: String? = null,

	@field:SerializedName("media")
	val media: List<Any?>? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("commentCount")
	val commentCount: Int? = null,

	@field:SerializedName("avgPoint")
	val avgPoint: Float? = null,

	@field:SerializedName("shareCount")
	val shareCount: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("expressive")
	val expressive: String? = null,

	@field:SerializedName("meta")
	val meta: Any? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@field:SerializedName("page")
	val page: Any? = null,

	@field:SerializedName("viewCount")
	val viewCount: Int? = null,

	@field:SerializedName("user")
	val user: Any? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("disableNotify")
	val disableNotify: Any? = null
)

data class PostData(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("rows")
	val rows: List<ICPost>? = null
)
