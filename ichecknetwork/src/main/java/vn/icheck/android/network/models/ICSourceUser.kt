package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICSourceUser(
        @SerializedName("id") val id: Long,
        @SerializedName("icheckId") val icheckId: Long,
        @SerializedName("firstName") val firstName: String?,
        @SerializedName("lastName") val lastName: String?,
        @SerializedName("avatar") val avatar: String?,
        @SerializedName("avatarThumbnail") val avatarThumbnail: ICThumbnail?,
        @SerializedName("rank") val rank: Int,
        @SerializedName("titleId") val titleId: Long,
        @SerializedName("roleId") val roleId: Long?,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?,
        @SerializedName("deletedAt") val deletedAt: String?
)