package vn.icheck.android.model.cart

import com.google.gson.annotations.SerializedName

data class CartResponse(

		@field:SerializedName("shop")
	val shop: Shop? = null,

		@field:SerializedName("products")
	val itemCart: List<ItemCartItem?>? = null,

		@field:SerializedName("userId")
	val userId: Long? = null
)

data class Shop(

	@field:SerializedName("cover")
	val cover: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@field:SerializedName("avatar")
	val avatar: String? = null
)

data class Product(

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@SerializedName("originId")
	var originId : Long? = null,

	@SerializedName("productDataHubId")
	var productDataHubId: Long? = null
)

data class ItemCartItem(

	@field:SerializedName("product")
	val product: Product? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("price")
	val price: Long? = null,

	@field:SerializedName("originPrice")
	val originPrice:Long? = null,

	@field:SerializedName("productTotal")
	val productTotal:Long? = null
){
	var isSelected = true
}
