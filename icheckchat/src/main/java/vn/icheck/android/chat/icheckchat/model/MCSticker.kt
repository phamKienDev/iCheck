package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class MCSticker : Serializable {

    @Expose
    val id: Long? = null

    @Expose
    val key: String? = null

    @Expose
    val thumbnail: String? = null

    @Expose
    val packageId: Long? = null

    @Expose
    val packages: MCSticker? = null
}