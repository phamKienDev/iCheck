package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICPayment(
        @SerializedName("id") val id: Int,
        @SerializedName("provider") val provider: String,
        @SerializedName("name") val name: String,
        @SerializedName("is_active") val isActive: Boolean,
        @SerializedName("fee_fixed") val feeFixed: Int,
        @SerializedName("fee_percent") val feePercent: Float
)