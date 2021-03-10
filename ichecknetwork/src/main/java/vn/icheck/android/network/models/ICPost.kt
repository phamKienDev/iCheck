package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.wall.ICUserPrivacyConfig
import java.io.Serializable

data class ICPost(
        @SerializedName("id") val id: Long,
        @SerializedName("userId") val userId: Long,
        @SerializedName("pageId") val pageId: Long,
        @SerializedName("content") val content: String?,
        @SerializedName("attachment") val attachment: List<ICThumbnail>?,
        @SerializedName("media") val media: List<ICMedia>?,
        @SerializedName("targetType") val targetType: String?,
        @SerializedName("targetId") val targetId: Long?,
        @SerializedName("involveType") val involveType: String?,
        @SerializedName("involveId") val involveId: Long?,
        @SerializedName("meta") val meta: ICPostMeta?,
        @SerializedName("privacy") val privacy: List<ICPrivacy>?,
        @SerializedName("commentCount") var commentCount: Int,
        @SerializedName("expressiveCount") var expressiveCount: Int,
        @SerializedName("shareCount") val shareCount: Int,
        @SerializedName("viewCount") val viewCount: Int,
        @SerializedName("avgPoint") val avgPoint: Float = 0f,
        @SerializedName("pinned") var pinned: Boolean,
        @SerializedName("createdAt") var createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?,
        @SerializedName("deletedAt") val deletedAt: String?,
        @SerializedName("user") val user: ICUserPost?,
        @SerializedName("page") val page: ICRelatedPage?,
        @SerializedName("customerCriteria") var customerCriteria: List<ICCriteriaReview>?,
        @SerializedName("expressive") var expressive: String?,
        @SerializedName("disableNotify") var disableNotify: Boolean = false,
        @SerializedName("comments") var comments: MutableList<ICCommentPost>? = null,
        @SerializedName("link") var link: String? = null,
        var marginTop: Int = 0,
        var positionMedia: Int = -1
) : Serializable