package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICAdsData(
        @SerializedName("id") val id: String,
        @SerializedName("targetType") val targetType: String?,
        @SerializedName("targetId") val targetId: String?,
        @SerializedName("media") var media: MutableList<ICMedia>?,
        @SerializedName("avatar") val avatar: ICMedia?,
        @SerializedName("barcode") val barcode: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("expireTime") val expireTime: Int?,
        @SerializedName("price") val price: Long?,
        @SerializedName("sellPrice ") val sellPrice: Long?,
        @SerializedName("verified") val verified: Boolean?,
        @SerializedName("rating") val rating: Double?,
        @SerializedName("ratingText") val ratingText: String?,
        @SerializedName("reviewCount") val reviewCount: Long?,
        @SerializedName("followCount") val followCount: Long?,
        @SerializedName("description") val description: String?,
        @SerializedName("startTime") val startTime: String?,
        @SerializedName("endTime") val endTime: String?,
        @SerializedName("remainingTime") val remainingTime: Int?,
        @SerializedName("owner") val owner: ICAdsOwner?,
        @SerializedName("state") val state: Int = 0,
        @SerializedName("hasOnboarding") var hasOnboarding: Boolean = false,
        var isFollow: Boolean = false
) : Serializable