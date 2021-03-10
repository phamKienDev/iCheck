package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICCriteriaShop(
        @SerializedName("id") val id: Long,
        @SerializedName("set_id") val set_id: Long,
        @SerializedName("criteria_id") val criteria_id: Long,
        @SerializedName("weight") val weight: Float,
        @SerializedName("criteria") val criteria: Criteria,
        @SerializedName("position") val position: Int
) {
    data class Criteria(
            @SerializedName("id") val id: Long,
            @SerializedName("name") val name: String,
            @SerializedName("code") val code: String,
            @SerializedName("description") val description: String?,
            @SerializedName("created_at") val created_at: String,
            @SerializedName("updated_at") val updated_at: String,
            var rating: Float = 0F
    )
}