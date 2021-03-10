package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICBookmark(
        @SerializedName("id") val id: Long,
        @SerializedName("userId") val userId: Long,
        @SerializedName("productId") val productId: Long,
//        @SerializedName("albumId") val albumId: ,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?
)