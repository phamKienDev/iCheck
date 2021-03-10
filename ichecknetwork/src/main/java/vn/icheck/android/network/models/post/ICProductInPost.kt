package vn.icheck.android.network.models.post

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICThumbnail

data class ICProductInPost(
        @Expose
        var id: Long?,
        @Expose
        var avatar_product: String?,
        @Expose
        var name_product: String?,
        @Expose
        var name_busniness: String?,
        @Expose
        var image_product: MutableList<ICThumbnail>?,
        @Expose
        var video: String?
)