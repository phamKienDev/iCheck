package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICProductQuestion(
        @Expose val id: Long = -1,
        @Expose var user: ICUserPost? = null,
        @Expose val page: ICPage? = null,
        @Expose val targetType: String? = null,
        @Expose val targetId: Long? = null,
        @Expose val involveType: String? = null,
        @Expose var content: String? = null,
        @Expose val media: List<ICMedia>? = null,
        @Expose val avgPoint: Int = 0,
        @Expose val expressiveCount: Int = 0,
        @Expose val shareCount: Int = 0,
        @Expose val viewCount: Int = 0,
        @Expose val replyCount: Int = 0,
        @Expose var replies: List<ICProductQuestion>? = null,
        @Expose var expressive: String? = null,
        @Expose var createdAt: String? = null,
        @Expose val updatedAt: String? = null,
        @Expose val disabeNotify: Boolean? = null,
        var parentID: Long? = null,
        var marginTop: Int = 0,
        var marginStart: Int = 0,
        var isReply: Boolean = false
) : Serializable