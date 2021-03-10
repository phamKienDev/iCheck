package vn.icheck.android.network.models

data class ICOrderHistoryV2(
		val note: String? = null,
		val createdAt: String? = null,
		val updatedAt: String? = null,
		val cancelledAt: String? = null,
		val completedAt: String? = null,
		val code: String? = null,
		val phone: String? = null,
		val orderItem: MutableList<OrderItemItem>? = null,
		val shippingAddress: ICAddress? = null,
		val shippingAddressId: Int? = null,
		val id: Long? = null,
		val billingAddress: ICAddress? = null,
		val email: String? = null,
		val customer: ICCustomer? = null,
		val status: Int? = null,
		val shop: ICShop? = null
)

data class OrderItemItem(
	val quantity: Int? = null,
	val price: Int? = null,
	var id: Long? = null,
	val originPrice: Int? = null,
	val productInfo: ProductInfo? = null
)

data class ProductInfo(
	val price: Int? = null,
	val imageUrl: String? = null,
	val name: String? = null,
	val id: Int? = null
)
