package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICThumbnailAttachment(
    @Expose var thumbnail: String? = null,
    @Expose var original: String? = null,
    @Expose var small: String? = null,
    @Expose var medium: String? = null,
    @Expose var square: String? = null
)