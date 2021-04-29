package vn.icheck.android.network.model.cart

import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.ICCustomer
import vn.icheck.android.network.models.ICShop

data class PurchasedOrderResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("shippingAddressId")
	val shippingAddressId: Int? = null,

	@field:SerializedName("id")
	val id: Long? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("customer")
	val customer: ICCustomer? = null,

	@field:SerializedName("shop")
	val shop: ICShop? = null
)
