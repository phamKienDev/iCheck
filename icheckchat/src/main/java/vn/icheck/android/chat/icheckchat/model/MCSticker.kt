package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class MCSticker : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    val key: String? = null

    @Expose
    var thumbnail: String? = null

    @Expose
    var packageId: Long? = null

    @Expose
    val packages: MCSticker? = null
}