package vn.icheck.android.model.reports

import com.google.gson.annotations.SerializedName

data class ReportUserCategoryResponse(

	@field:SerializedName("data")
	val data: ReportUserCategoryData? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class ReportUserCategoryDataItem(

	@field:SerializedName("reportType")
	val reportType: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("deletedAt")
	val deletedAt: Any? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order")
	val order: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class ReportUserCategoryData(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("rows")
	val rows: List<ReportUserCategoryDataItem?>? = null
)
