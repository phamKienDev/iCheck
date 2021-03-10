package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICAdsNew(
        @SerializedName("id") val id: Long,
        @SerializedName("campaignId") val campaignId: Long,
        @SerializedName("name") val name: String,
        @SerializedName("objectType") val objectType: String,
        @SerializedName("data") val data: MutableList<ICAdsData>,
        @SerializedName("type") var type: String,
        @SerializedName("targetType") val targetType: String?,
        @SerializedName("targetId") val targetId: String?
) : Serializable