package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICThumbnail (
        @Expose var id: String? = null,
        @Expose var thumbnail: String? = null,
        @Expose var original: String? = null,
        @Expose var small: String? = null,
        @Expose var medium: String? = null,
        @Expose var square: String? = null
): Serializable