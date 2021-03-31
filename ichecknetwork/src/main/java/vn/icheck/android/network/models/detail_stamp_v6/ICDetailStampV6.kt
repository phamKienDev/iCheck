package vn.icheck.android.network.models.detail_stamp_v6

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICProductLink
import java.io.Serializable

class ICDetailStampV6 : Serializable {
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: ICObjectDetailStampV6? = null

    class ICObjectDetailStampV6 : Serializable {
        @Expose
        var type:String? = null
        @Expose
        var message:String? = null
        @Expose
        var stamp : ICObjectStampV6? = null
        @Expose
        var messages:MutableList<ICObjectMessageV6>? = null
        @Expose
        var business: ICObjectBusinessV6? = null
        @Expose
        var product: ICObjectProductV6? = null
        @Expose
        var other_product:MutableList<ICObjectOtherProductV6>? = null
        @Expose
        val product_link: MutableList<ICProductLink>? = null
    }
}