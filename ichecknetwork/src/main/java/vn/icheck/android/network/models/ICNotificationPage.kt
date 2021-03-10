package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICNotificationPage(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("attachments") val attachments: MutableList<ICAttachments>?,
        @SerializedName("objectType") val objectType: String?,
        @SerializedName("pageOverview") val pageOverview: ICPage?,
        @SerializedName("pageDetail") val pageDetail: ICPage?,
        @SerializedName("ownerId") val ownerId: Long?,
        @SerializedName("userFollowIdList") val userFollowIdList:List<Long>? = null,
        @SerializedName("owner") val owner: ICOwner?,
        @SerializedName("templateId") val templateId: Long?,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?,
        @SerializedName("deletedAt") val deletedAt: String?,
        @SerializedName("avatar") val avatar:String?,
        var isFollow: Boolean? = null
)