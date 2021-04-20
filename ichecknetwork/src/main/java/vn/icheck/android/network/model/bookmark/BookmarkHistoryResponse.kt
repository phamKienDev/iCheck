package vn.icheck.android.network.model.bookmark

import com.google.gson.annotations.SerializedName

data class BookmarkHistoryResponse(

	@field:SerializedName("sourceId")
	val sourceId: String? = null,

	@field:SerializedName("owner")
	val owner: Owner? = null,

	@field:SerializedName("questionCount")
	val questionCount: Int? = null,

	@field:SerializedName("verified")
	val verified: Boolean? = null,

	@field:SerializedName("rating")
	val rating: Double? = null,

	@field:SerializedName("media")
	val media: List<MediaItem?>? = null,

	@field:SerializedName("setCriteria")
	val setCriteria: List<SetCriteriaItem?>? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("deletedAt")
	val deletedAt: Any? = null,

	@field:SerializedName("reviewCount")
	val reviewCount: Int? = null,

	@field:SerializedName("price")
	val price: Long? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("scanCount")
	val scanCount: String? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("categories")
	val categories: List<CategoriesItem?>? = null,

	@field:SerializedName("barcode")
	val barcode: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class MediaItem(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)

data class Owner(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("verified")
	val verified: Boolean? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("avatar")
	val avatar: String? = null,

	@field:SerializedName("title")
	val title: Any? = null,

	@field:SerializedName("pageId")
	val pageId: Int? = null
)

data class CategoriesItem(

	@field:SerializedName("level")
	val level: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class SetCriteriaItem(

	@field:SerializedName("totalPoint")
	val totalPoint: Int? = null,

	@field:SerializedName("criteriaId")
	val criteriaId: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("weight")
	val weight: Double? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null
)
