package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICCheckoutOrder(
        @SerializedName("shop_id") val shop_id: Long,
        @SerializedName("shop") val shop: ICShop,
        @SerializedName("shipping_method_id") val shipping_method_id: Int,
        @SerializedName("items") val items: MutableList<ICItemCart>,
        @SerializedName("shipping_methods") val shipping_methods: MutableList<ICShipping>,
        @SerializedName("payment_fee") val payment_fee: Long,
        @SerializedName("shipping_amount") val shipping_amount: Long,
        @SerializedName("sub_total") val sub_total: Long,
        @SerializedName("coupon_discount") val coupon_discount: Int,
        @SerializedName("grand_total") val grand_total: Long,
        @SerializedName("note") var note: String?
)