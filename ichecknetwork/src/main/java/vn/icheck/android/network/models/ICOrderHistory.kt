package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICOrderHistory(
        @SerializedName("id") val id: Long,
        @SerializedName("number") val number: String,
        @SerializedName("shop_id") val shop_id: Long,
        @SerializedName("customer_id") val customer_id: Long,
        @SerializedName("coupon_id") val coupon_id: Long?,
        @SerializedName("note") val note: String?,
        @SerializedName("status") val status: Int,
        @SerializedName("shipping_method") val shipping_method: ICShippingMethod,
        @SerializedName("payment_method") val payment_method: ICPayment,
        @SerializedName("payment_fee") val payment_fee: Long,
        @SerializedName("sub_total") val sub_total: Long,
        @SerializedName("shipping_amount") val shipping_amount: Long,
        @SerializedName("grand_total") val grand_total: Long,
        @SerializedName("total_refunded") val total_refunded: Long,
        @SerializedName("coupon_discount") val coupon_discount: Long,
        @SerializedName("cancel_reason") val cancel_reason: String?,
        @SerializedName("shipping_address") val shipping_address: ICAddress,
        @SerializedName("billing_address") val billing_address: ICAddress,
        @SerializedName("estimated_delivery_date_from") val estimated_delivery_date_from: String?,
        @SerializedName("estimated_delivery_date_to") val estimated_delivery_date_to: String?,
        @SerializedName("phone") val phone: String,
        @SerializedName("cancelled_at") val cancelled_at: String?,
        @SerializedName("completed_at") val completed_at: String?,
        @SerializedName("shop") val shop: ICShop?,
        @SerializedName("created_at") val created_at: String?,
        @SerializedName("updated_at") val updated_at: String?,
        @SerializedName("ship_failed_reason") val ship_failed_reason: String?,
        @SerializedName("reward") val reward: String?
)