package vn.icheck.android.model.wall

import com.google.gson.annotations.SerializedName

data class LayoutResponse(

	@field:SerializedName("layout")
	val layout: List<LayoutItem?>? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class LayoutItem(

	@field:SerializedName("request")
	val request: Request? = null,

	@field:SerializedName("custom")
	val custom: Custom? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("key")
	val key: Any? = null
)

data class Custom(
	val any: Any? = null
)

data class Request(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("url")
	val url: String? = null
){
	fun getSocialUrl() = "/social/api$url"
}
