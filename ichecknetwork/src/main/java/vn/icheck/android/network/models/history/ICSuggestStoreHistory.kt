package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICLocation
import java.io.Serializable

data class ICSuggestStoreHistory(
        @Expose var cover: String? = null,
        @Expose var id: Long? = null,
        @Expose var verified: Boolean? = null,
        @Expose var avatar: String? = null,
        @Expose var name: String? = null,
        @Expose var location: ICLocation? = null,
        @Expose var pageId: Long? = null,
        @Expose var rating: Float? = null,
        @Expose var reviewCount: Int? = null,
        @Expose var numProductSell: Int? = null
) : Serializable
