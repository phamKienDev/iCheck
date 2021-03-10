package vn.icheck.android.network.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class ICCommentPost(
        @Expose val id: Long = 0,
        @Expose var user: ICCustomer? = null,
        @Expose val page: ICPage? = null,
        @Expose var content: String? = null,
        @Expose val media: List<ICMedia?>? = null,
        @Expose var expressiveCount: Int = 0,
        @Expose val commentId: Long? = null,
        @Expose val replyCount: Int = 0,
        @Expose var replies: MutableList<ICCommentPost>? = null,
        @Expose val hidden: Boolean? = null,
        @Expose var expressive: String? = null,
        @Expose val createdAt: String? = null,
        @Expose val updatedAt: String? = null,
        var involveType: String? = null,
        var parentID: Long? = null,
        var marginStart: Int = 0,
        var marginTop: Int = 0,
        var isReply: Boolean = false,
        var postId: Long = 0
) : Serializable