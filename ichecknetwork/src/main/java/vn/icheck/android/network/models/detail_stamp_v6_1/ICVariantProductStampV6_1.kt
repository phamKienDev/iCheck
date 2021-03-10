package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICVariantProductStampV6_1 : Serializable {
    @Expose
    var error: Boolean? = null

    @Expose
    var status: Int? = null

    @Expose
    var data: ICVariant? = null

    class ICVariant : Serializable {
        @Expose
        var products: MutableList<ICObjectVariant>? = null

        class ICObjectVariant : Serializable {
            @Expose
            var id: Long? = null

            @Expose
            var product_id: Long? = null

            @Expose
            var extra: String? = null

            @Expose
            var image: String? = null
        }
    }
}