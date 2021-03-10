package vn.icheck.android.network.models.product_need_review

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICOwner
import vn.icheck.android.network.models.ICThumbnail
import java.io.Serializable

data class ICProductNeedReview(
        @Expose var id: Long? = null,
        @Expose var name: String? = null,
        @Expose var barcode: String? = null,
        @Expose var price: Long? = null,
        @Expose var bestPrice: Long? = null,
        @Expose var media: MutableList<ICMedia>? = null,
        @Expose var verified: Boolean? = null,
        @Expose var visibled: Boolean? = null,
        @Expose var rating: Float = 0F,
        @Expose var owner: ICOwner? = null,
        @Expose var categoryIds : MutableList<Int>? = null
) : Serializable