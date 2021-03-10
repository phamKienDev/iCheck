package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICStatusHistory(
        @SerializedName("id") val id: Long,
        @SerializedName("order_id") val order_id: Long,
        @SerializedName("comment") val comment: String,
        @SerializedName("status") val status: Int,
        @SerializedName("shipping_partner") val shippingPartner: String?,
        @SerializedName("reason") val reason: String?,
        @SerializedName("created_at") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?
)