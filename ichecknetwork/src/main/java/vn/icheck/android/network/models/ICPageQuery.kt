package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICPageQuery(
        @SerializedName("attachments") val attachments: List<ICAttachments?>? = null,
        @SerializedName("followCount") var followCount: Int = 0,
        @SerializedName("mail") val mail: String? = null,
        @SerializedName("rating") val rating: Double? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("likeCount") val likeCount: Int = 0,
        @SerializedName("productCount") val productCount: Int? = null,
        @SerializedName("ownerId") val ownerId: Int? = null,
        @SerializedName("templateId") val templateId: Int? = null,
        @SerializedName("isVerify") val isVerify: Boolean = false,
        @SerializedName("objectType") val objectType: String? = null,
        @SerializedName("createdAt") val createdAt: String? = null,
        @SerializedName("id") val id: Long,
        @SerializedName("updatedAt") val updatedAt: String? = null,
        @SerializedName("website") val website: String? = null,
        @SerializedName("address") val address: String? = null,
        @SerializedName("avatar") val avatar: String? = null,
        @SerializedName("parentId") val parentId: Int? = null,
        @SerializedName("deletedAt") val deletedAt: String? = null,
        @SerializedName("followers") val followers: List<ICTargetUser?>? = null,
        @SerializedName("phone") val phone: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("scanCount") val scanCount: Int? = null,
//        @SerializedName("location") val location: ICLocation? = null,
        @SerializedName("status") var status: Boolean = false,
        @SerializedName("isOffline") var isOffline: Boolean = false,
        @SerializedName("isOnline") var isOnline: Boolean = false
) : Serializable


