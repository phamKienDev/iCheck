package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICNotification(
        @SerializedName("id") val id: Long,
        @SerializedName("sourceUserId") val sourceUserId: Long,
        @SerializedName("targetUserId") val targetUserId: Long,
        @SerializedName("targetId") val targetId: Long?,
        @SerializedName("targetName") val targetName: String?,
        @SerializedName("targetType") val targetType: String?,
        @SerializedName("entityId") val entityId: Long,
        @SerializedName("entityName") val entityName: String?,
        @SerializedName("entityType") val entityType: Int,
        @SerializedName("notificationType") val notificationType: Int,
        @SerializedName("title") val title: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("path") val path: String?,
        @SerializedName("status") var status: Int,
        @SerializedName("sources") val sourceUser: List<ICUser>?,
        @SerializedName("targetUser") val targetUser: ICUser?,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?,
        @SerializedName("deletedAt") val deletedAt: String?,
        @SerializedName("eventType") val eventType: String?,
        @SerializedName("objectId") val objectId: Long?,
        @SerializedName("type") val type: String?,
        @SerializedName("link") val link: String?,
        @SerializedName("unsubscribeNoticeEventId") var unsubscribeNoticeEventId: Int?,
        @SerializedName("action") val action: String?,
        @SerializedName("target") val target:NotificationTarget?,
        @SerializedName("redirectPath") val redirectPath: String?,
        @SerializedName("targetEntity") val targetEntity: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("isReaded") var isReaded: Boolean?,
        @SerializedName("isTurnOff") var isTurnOff: Boolean?

){
    var showTitle = false
}

data class NotificationTarget(
        @SerializedName("id") val id:Long,
        @SerializedName("owner") val owner:Long,
        @SerializedName("entity") val entity:String
)
