package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.base.ICResponseCode
import java.io.Serializable

class ICSetting : Serializable {

    @Expose
    val userId: Long? = null

    @Expose
    val CAMPAIGN: Boolean = false

    @Expose
    val MESSAGE: Boolean = false

    @Expose
    val NEWS: Boolean = false

    @Expose
    val message: String? = null

    @Expose
    val status: Long? = null
}