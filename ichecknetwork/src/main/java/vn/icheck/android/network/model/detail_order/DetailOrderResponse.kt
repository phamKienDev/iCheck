package vn.icheck.android.network.model.detail_order

import com.google.gson.annotations.SerializedName

data class DetailOrderResponse(

        @field:SerializedName("note")
        val note: String? = null,

        @field:SerializedName("shop")
        val shop: Shop? = null,

        @field:SerializedName("code")
        val code: String? = null,

        @field:SerializedName("orderItem")
        val orderItem: List<OrderItemItem?>? = null,

        @field:SerializedName("shippingAddressId")
        val shippingAddressId: Long? = null,

        @field:SerializedName("createdAt")
        val createdAt: String? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("shippingAddress")
        val shippingAddress: ShippingAddress? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("billingAddress")
        val billingAddress: BillingAddress? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("customer")
        val customer: Customer? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("deliveryCharges")
        val deliveryCharges: Long? = null,

        @field:SerializedName("updatedAt")
        val updatedAt:String? = null,

        @field:SerializedName("completedAt")
        val completedAt:String?,

        @field:SerializedName("cancelledAt")
        val cancelledAt:String?
)

data class Customer(

        @field:SerializedName("firstName")
        val firstName: String? = null,

        @field:SerializedName("lastName")
        val lastName: String? = null,

        @field:SerializedName("blocked")
        val blocked: Boolean? = null,

        @field:SerializedName("gender")
        val gender: String? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("avatar")
        val avatar: String? = null,

        @field:SerializedName("email")
        val email: String? = null
)

data class BillingAddress(

        @field:SerializedName("firstName")
        val firstName: String? = null,

        @field:SerializedName("lastName")
        val lastName: String? = null,

        @field:SerializedName("zipCode")
        val zipCode: Int? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("city")
        val city: City? = null,

        @field:SerializedName("address1")
        val address1: String? = null,

        @field:SerializedName("district")
        val district: District? = null,

        @field:SerializedName("state")
        val state: Int? = null,

        @field:SerializedName("ward")
        val ward: Ward? = null,

        @field:SerializedName("email")
        val email: String? = null
)

data class City(

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("countryId")
        val countryId: Int? = null
)

data class Shop(

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("verified")
        val verified: Boolean? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("avatar")
        val avatar: String? = null,

        @field:SerializedName("pageId")
        val pageId: Long? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("cover")
        val cover: String? = null
)

data class ProductInfo(

        @field:SerializedName("price")
        val price: Int? = null,

        @field:SerializedName("imageUrl")
        val imageUrl: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("originId")
        val originId: Long? = null,

        @field:SerializedName("id")
        val id: Long? = null
)

data class OrderItemItem(

        @field:SerializedName("quantity")
        val quantity: Int? = null,

        @field:SerializedName("price")
        val price: Int? = null,

        @field:SerializedName("id")
        val id: Long? = null,



        @field:SerializedName("originPrice")
        val originPrice: Long? = null,

        @field:SerializedName("productInfo")
        val productInfo: ProductInfo? = null
)

data class District(

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("cityId")
        val cityId: Int? = null
)

data class Ward(

        @field:SerializedName("districtId")
        val districtId: Int? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null
)

data class ShippingAddress(

        @field:SerializedName("firstName")
        val firstName: String? = null,

        @field:SerializedName("lastName")
        val lastName: String? = null,

        @field:SerializedName("zipCode")
        val zipCode: Long? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("city")
        val city: City? = null,

        @field:SerializedName("address1")
        val address1: String? = null,

        @field:SerializedName("district")
        val district: District? = null,

        @field:SerializedName("state")
        val state: Long? = null,

        @field:SerializedName("ward")
        val ward: Ward? = null,

        @field:SerializedName("email")
        val email: String? = null
) {
    fun getFullAddress() = "${address}, ${ward?.name}, ${district?.name}, ${city?.name}"

    fun getName() = "$lastName  $firstName"
}
