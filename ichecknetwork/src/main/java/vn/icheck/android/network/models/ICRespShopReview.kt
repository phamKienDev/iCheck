package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICRespShopReview(
        @SerializedName("id") val id: Long,
        @SerializedName("ocsv_id") val ocsv_id: Long,
        @SerializedName("customer_id") val customer_id: Long,
        @SerializedName("object_id") val object_id: Long,
        @SerializedName("object_type") val object_type: String,
        @SerializedName("message") val message: String,
        @SerializedName("average_point") val average_point: Float,
        @SerializedName("useful") val useful: Int,
        @SerializedName("unuseful") val unuseful: Int,
        @SerializedName("is_hidden") val is_hidden: Boolean,
        @SerializedName("is_pinned") val is_pinned: Boolean,
        @SerializedName("created_at") val created_at: String,
        @SerializedName("updated_at") val updated_at: String,
        @SerializedName("images") val images: MutableList<String>?,
        @SerializedName("image_thumbs") val image_thumbs: MutableList<ICThumbnail>?
)