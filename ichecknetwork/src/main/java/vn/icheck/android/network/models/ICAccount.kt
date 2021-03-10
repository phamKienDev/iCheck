package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICAccount(
        @Expose var id: Long = 0,
        @Expose var type: String? = null,
        @Expose var name: String? = null,
        @Expose var avatar: String? = null,
        @Expose var verified: Boolean = false,
        @Expose var avatar_thumbnails: ICThumbnail? = null
)