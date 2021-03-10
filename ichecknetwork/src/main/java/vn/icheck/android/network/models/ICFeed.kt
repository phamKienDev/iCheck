package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.post.ICProductInPost

data class ICFeed (
        @Expose val id: Long = 0,
        @Expose val avatar: ICThumbnail? = null,
        @Expose val user_name: String? = null,
        @Expose val user_level: String? = null,
        @Expose val rating: Float = 0f,
        @Expose val content: String? = null,
        @Expose val product: ICProductInPost? = null,
        @Expose val isLike: Boolean? = null,
        @Expose val date_time: String? = null,
        @Expose val isFollow: Boolean? = null,
        @Expose val isNotification: Boolean? = true
)