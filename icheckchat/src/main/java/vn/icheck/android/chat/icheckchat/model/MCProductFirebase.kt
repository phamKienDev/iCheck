package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class MCProductFirebase : Serializable {

    @Expose
    var barcode: String? = null

    @Expose
    var name: String? = null

    @Expose
    var image: String? = null

    @Expose
    var price: Long? = null

    @Expose
    var productId: Long? = null

    @Expose
    var state: String? = null
}