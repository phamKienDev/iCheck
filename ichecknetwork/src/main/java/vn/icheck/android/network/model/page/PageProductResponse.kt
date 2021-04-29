package vn.icheck.android.network.model.page

import com.google.gson.annotations.SerializedName

data class PageProductResponse(

	@field:SerializedName("data")
	val data: PageProductData? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class ProductItem(

	@field:SerializedName("reviewCount")
	val reviewCount: Int? = null,

	@field:SerializedName("price")
	val price: Long? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("rating")
	val rating: Any? = null,

	@field:SerializedName("verified")
	val verified: Boolean? = null,

	@field:SerializedName("media")
	val media: List<MediaItem>? = null,

	@field:SerializedName("barcode")
	val barcode: String? = null
)

data class MediaItem(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)

data class PageProductData(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("rows")
	val rows: List<ProductItem?>? = null
)
