package vn.icheck.android.network.model.category

import com.google.gson.annotations.SerializedName

data class IckCategoryResponse(

		@field:SerializedName("data")
		val data: CategoryData? = null,

		@field:SerializedName("statusCode")
		val statusCode: String? = null
)

data class UpdatedAt(

		@field:SerializedName("value")
		val value: String? = null
)

data class AttributeSet(

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("id")
		val id: Int? = null
)

data class CategoryItem(

		@field:SerializedName("createdAt")
		val createdAt: String? = null,

		@field:SerializedName("orderSort")
		val orderSort: Int? = null,

		@field:SerializedName("level")
		val level: Int? = null,

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("id")
		val id: Long? = null,

		@field:SerializedName("attributeSet")
		val attributeSet: AttributeSet? = null,

		@field:SerializedName("updatedAt")
		val updatedAt: UpdatedAt? = null,

		@field:SerializedName("image")
		val image: String? = null,

		@field:SerializedName("categoryDescendant")
		val categoryDescendant: List<CategoryDescendantItem?>? = null,

		@field:SerializedName("childrenCount")
		val childrenCount:Int? = null
)

data class CategoryData(

		@field:SerializedName("count")
		val count: Int? = null,

		@field:SerializedName("rows")
		val rows: List<CategoryItem>? = null
)

data class CategoryDescendantItem(

		@field:SerializedName("level")
		val level: Int? = null,

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("categoryId")
		val categoryId: Int? = null
)
